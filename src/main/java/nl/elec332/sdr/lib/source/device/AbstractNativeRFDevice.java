package nl.elec332.sdr.lib.source.device;

import nl.elec332.sdr.lib.api.source.IRFInputSource;

/**
 * Created by Elec332 on 27-3-2020
 */
public abstract class AbstractNativeRFDevice<T> extends AbstractNativeDevice<T> implements IRFInputSource<T> {

    @Override
    public final void setFrequency(long freq) {
        final long f = setDeviceFrequency(freq);
        iterateListeners(l -> l.onFrequencyChanged(f));
    }

    protected abstract long setDeviceFrequency(long freq);

    @Override
    public final void setSampleRate(long sampleRate) {
        final long s = setDeviceSampleRate(sampleRate);
        iterateListeners(l -> l.onSampleRateChanged(s));
    }

    protected abstract long setDeviceSampleRate(long sampleRate);

    @Override
    public final void setLNAGain(int gain) {
        final int g = setDeviceLNAGain(gain);
        iterateListeners(l -> l.onLNAGainChanged(g));
    }

    protected abstract int setDeviceLNAGain(int gain);

}
