package nl.elec332.sdr.lib.api.datastream;

/**
 * Created by Elec332 on 7-2-2020
 */
public interface IDataProcessingStep {

    void process(ISampleData in, ISampleData out);

    int getOutputBufferSize(int inputSamples);

}
