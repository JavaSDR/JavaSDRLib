package nl.elec332.sdr.lib.dsp.demodulation;

import nl.elec332.sdr.lib.api.datastream.IDataProcessingStep;
import nl.elec332.sdr.lib.api.datastream.ISampleData;
import nl.elec332.sdr.lib.extensions.SDRLibraryExtensions;

/**
 * Created by Elec332 on 31-1-2020
 */
public class AMDemodulator implements IDataProcessingStep {

    @Override
    public void process(ISampleData in, ISampleData out) {
        SDRLibraryExtensions.getUtilLibrary().am(in.getData(), out.getData());
    }

    @Override
    public int getOutputBufferSize(int inputSamples) {
        return inputSamples;
    }

}
