package tasks;

import java.util.List;
import java.util.concurrent.Callable;

public abstract class ReduceTask<I, O> implements Callable<O> {
    private final List<I> listValues;

    public ReduceTask(List<I> listValues) {
        this.listValues = listValues;
    }

    /**
     *  Abstract function which receives a list of I type values and return a O type value after a
     * certain logic of reducing
     * @param listValues the values which need to be reduced
     * @return the value resulted after reduction
     */

    public abstract O reduce(List<I> listValues);

    /**
     * Call function which run reduce logic. It is used for the parallel logic.
     * @return the mapped value
     */

    @Override
    public O call() {
        return reduce(listValues);
    }
}
