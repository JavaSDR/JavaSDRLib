package nl.elec332.sdr.lib.dsp.fft;

import nl.elec332.sdr.lib.api.fft.IFFTPlan;
import nl.elec332.sdr.lib.api.fft.IIQFFTProcessor;
import nl.elec332.sdr.lib.api.util.IDoubleArray;
import nl.elec332.sdr.lib.extensions.SDRLibraryExtensions;

/**
 * Created by Elec332 on 13-2-2020
 */
public class IQFFT implements IIQFFTProcessor {

    public IQFFT(int samples) {
        this.fftBuf = SDRLibraryExtensions.createNewArray(samples * 2);
        this.plan = SDRLibraryExtensions.getFFTLibrary().createPlan(samples, fftBuf, true);
    }

    private final IDoubleArray fftBuf;
    private final IFFTPlan plan;

    @Override
    public void processIQData(IDoubleArray iq) {
        plan.execute(iq);
    }

    @Override
    public IDoubleArray getFFTBuffer() {
        return fftBuf;
    }

}
