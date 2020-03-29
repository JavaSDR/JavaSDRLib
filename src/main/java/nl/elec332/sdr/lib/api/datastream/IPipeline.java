package nl.elec332.sdr.lib.api.datastream;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Elec332 on 7-2-2020
 */
public interface IPipeline {

    IPipeline div2();

    default IPipeline modify(final Consumer<ISampleData> modifier) {
        return modify(pipeline -> modifier);
    }

    IPipeline modify(Function<IPipeline, Consumer<ISampleData>> modifier);

    default IPipeline modifyParallel(final Consumer<ISampleData> modifier) {
        return modifyParallel(pipeline -> modifier);
    }

    IPipeline modifyParallel(Function<IPipeline, Consumer<ISampleData>> modifier);

    default IPipeline andThen(final IDataProcessingStep step) {
        return andThen(pipeline -> step);
    }

    IPipeline andThen(Function<IPipeline, IDataProcessingStep> step);

    default IPipeline andThenParallel(final IDataProcessingStep step) {
        return andThenParallel(pipeline -> step);
    }

    IPipeline andThenParallel(Function<IPipeline, IDataProcessingStep> step);

    default IPipeline end(final Consumer<ISampleData> lastStep) {
        return end(pipeline -> lastStep);
    }

    IPipeline end(Function<IPipeline, Consumer<ISampleData>> lastStep);

    default IPipeline endParallel(final Consumer<ISampleData> lastStep) {
        return endParallel(pipeline -> lastStep);
    }

    IPipeline endParallel(Function<IPipeline, Consumer<ISampleData>> lastStep);

    boolean addChild(IPipeline child);

    default void addIntertwined(IPipeline obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        addChild(obj);
        obj.addChild(this);
    }

    int getSampleRate();

    int getSampleSize();

    void start();

    boolean isRunning();

    void stop();

}
