package tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class MapReduceLogic<I, M, O> {
    private final List<I> inputValues;
    private final Integer executorsNumber;

    public MapReduceLogic(List<I> inputValues, Integer executorsNumber) {
        this.inputValues = inputValues;
        this.executorsNumber = executorsNumber;
    }

    /**
     * Abstract function which maps a value of type I into a value of type M
     * @param value value which is mapped
     * @return result of the map function (an object of type M)
     */

    public abstract M map(I value);

    /**
     *  Abstract function which combine more objects type M into lists of objects type M after a
     * certain logic
     * @param listValues the values which need to be combined
     * @return a list of lists with the objects given
     */
    public abstract List<List<M>> combine(List<M> listValues);

    /**
     * Abstract function which reduce a list of M type values into an object of type O
     * @param listValues the list values which needed to be reduced
     * @return the O type object resulted in the M type values combination
     */

    public abstract O reduce(List<M> listValues);

    /**
     * Function which generalize the map-reduce logic:
     *  - create an executor with the fixed workers number
     *  - create and execute map logic tasks for every input value and extract the mapped values
     *  into a list
     *  - execute combine logic for the previous mapped results
     *  - create and execute reduce logic tasks for every list resulted from the combine step and
     *  extract the final reduced values
     * @return final reduced values
     */

    public List<O> runLogic() throws InterruptedException, ExecutionException {
        /*
            Create executor with fixed workers number
         */

        ExecutorService executor = Executors.newFixedThreadPool(executorsNumber);

        /*
            Create map logic tasks
         */

        List<MapTask<I, M>> mapTasks = new ArrayList<>();

        for (I inputValue : inputValues) {
            mapTasks.add(new MapTask<>(inputValue) {
                @Override
                public M map(I value) {
                    return MapReduceLogic.this.map(value);
                }
            });
        }

        /*
            Run map tasks and extract the values from the Future objects
         */

        List<Future<M>> futureValues = executor.invokeAll(mapTasks);

        List<M> mappedValues = new ArrayList<>();
        for (Future<M> futureValue : futureValues) {
            mappedValues.add(futureValue.get());
        }

        /*
            Run combine logic
         */

        List<List<M>> combinedValues = combine(mappedValues);

        /*
            Create reduce tasks
         */

        List<ReduceTask<M, O>> reduceTasks = new ArrayList<>();

        for (List<M> element : combinedValues) {
            reduceTasks.add(new ReduceTask<>(element) {
                @Override
                public O reduce(List<M> listValues) {
                    return MapReduceLogic.this.reduce(listValues);
                }
            });
        }

        /*
            Run reduce tasks and extract the values from the Future objects
         */

        List<Future<O>> futureFinalValues = executor.invokeAll(reduceTasks);
        executor.shutdown();

        List<O> reducedValues = new ArrayList<>();
        for (Future<O> futureValue : futureFinalValues) {
            reducedValues.add(futureValue.get());
        }

        return reducedValues;
    }
}
