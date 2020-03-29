package nl.elec332.sdr.lib.api.datastream;

import nl.elec332.sdr.lib.api.util.EndOfDataStreamException;

/**
 * Created by Elec332 on 8-2-2020
 */
public interface IDataSource {

    void readData(ISampleDataSetter dataSetter) throws EndOfDataStreamException;

    int getSampleRate();

    int getSampleSize();

    int getOutputBufferSize(int inputSamples);

    void stop();

    void addStopListener(Runnable listener);

    default long getFrequency() {
        return 0;
    }

}
