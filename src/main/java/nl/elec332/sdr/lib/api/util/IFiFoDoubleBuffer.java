package nl.elec332.sdr.lib.api.util;

import nl.elec332.sdr.lib.api.datastream.IPipeline;
import nl.elec332.sdr.lib.api.datastream.ISampleData;

import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

/**
 * Created by Elec332 on 27-1-2020
 */
public interface IFiFoDoubleBuffer extends Consumer<ISampleData> {

    @Override
    void accept(ISampleData data);

    double[] read(int length);

    double[] peek(int length);

    default void peek(IDoubleArray buffer) {
        peek(buffer.getBuffer());
    }

    void peek(double[] buffer);

    int size();

    void clear();

    default IPipeline createOutput(final int samples, int sampleRate) {
        return createOutput(obj -> true, () -> samples, samples, sampleRate);
    }

    default IPipeline createOutput(IntSupplier samples, int maxSamples, int sampleRate) {
        return createOutput(obj -> true, samples, maxSamples, sampleRate);
    }

    IPipeline createOutput(Predicate<IFiFoDoubleBuffer> validator, IntSupplier samples, int maxSamples, int sampleRate);

}
