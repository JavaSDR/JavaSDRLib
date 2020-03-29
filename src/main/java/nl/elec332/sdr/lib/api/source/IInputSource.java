package nl.elec332.sdr.lib.api.source;

import nl.elec332.sdr.lib.api.datastream.ISampleDataSetter;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 18-3-2020
 */
public interface IInputSource<PREBUF> {

    void startSampling(Consumer<Consumer<PREBUF>> bufferGetter);

    default void onRecordingStarted() {
    }

    void setSampleData(ISampleDataSetter sampleDataSetter, PREBUF buffer);

    void addListener(IInputEventListener<IInputSource<PREBUF>> listener);

    void stop();

    void close();

    int getSampleRate();

    int getSamplesPerBuffer();

    PREBUF createBuffer();

    default long getFrequency() {
        return 0;
    }

}
