package nl.elec332.sdr.lib.api.source;

/**
 * Created by Elec332 on 20-3-2020
 */
public interface IInputEventListener<T extends IInputSource<?>> {

    default void onRxStarted(T device) {
    }

    default void onRxStopped(T device) {
    }

    default void onClosed(T device) {
    }

    default void onFrequencyChanged(long freq) {
    }

    default void onSampleRateChanged(long sampleRate) {
    }

    default void onLNAGainChanged(int gain) {
    }

}
