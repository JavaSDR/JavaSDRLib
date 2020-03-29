package nl.elec332.sdr.lib.datastream.impl;

import nl.elec332.sdr.lib.api.datastream.IDataProcessingStep;
import nl.elec332.sdr.lib.api.datastream.ISampleData;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 9-2-2020
 */
public class RecycleDataProcessingStep implements IDataProcessingStep {

    public RecycleDataProcessingStep(Consumer<ISampleData> modifier) {
        this.modifier = modifier;
    }

    private final Consumer<ISampleData> modifier;

    @Override
    public void process(ISampleData in, ISampleData out) {
        this.modifier.accept(in);
    }

    @Override
    public int getOutputBufferSize(int inputSamples) {
        return -1;
    }

}
