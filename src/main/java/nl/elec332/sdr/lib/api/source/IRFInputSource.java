package nl.elec332.sdr.lib.api.source;

import nl.elec332.sdr.lib.api.datastream.ISampleDataSetter;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 18-3-2020
 */
public interface IRFInputSource<D> extends IInputSource<D> {

    void setFrequency(long freq);

    void setSampleRate(long sampleRate);

    void setLNAGain(int gain);

    long getFrequency();

    int getSampleRate();

    int getSamplesPerBuffer();

    D createBuffer();

    void startSampling(Consumer<Consumer<D>> bufferGetter);

    void setSampleData(ISampleDataSetter sampleDataSetter, D buffer);

    void stop();

    void close();

}
