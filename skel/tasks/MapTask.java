package tasks;

import java.util.concurrent.Callable;

public abstract class MapTask<I, O> implements Callable<O> {
    private final I initialValue;

    public MapTask(I initialValue) {
        this.initialValue = initialValue;
    }

    /**
     * Abstract function for map logic
     * @param value value which need to be mapped
     * @return the mapped value
     */

    public abstract O map(I value);

    /**
     * Call function which run map logic. It is used for the parallel logic.
     * @return the mapped value
     */

    @Override
    public O call() {
        return map(initialValue);
    }
}
