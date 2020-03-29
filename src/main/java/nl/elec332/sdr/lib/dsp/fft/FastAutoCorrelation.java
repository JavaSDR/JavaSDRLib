package nl.elec332.sdr.lib.dsp.fft;

import nl.elec332.sdr.lib.api.fft.IFFTPlan;
import nl.elec332.sdr.lib.api.fft.IIQFFTProcessor;
import nl.elec332.sdr.lib.api.util.IDoubleArray;
import nl.elec332.sdr.lib.extensions.SDRLibraryExtensions;

/**
 * Created by Elec332 on 31-1-2020
 */
public class FastAutoCorrelation implements IIQFFTProcessor {

    public FastAutoCorrelation(int sampleSize) {
        if (sampleSize % 2 != 0) {
            throw new IllegalArgumentException();
        }
        this.in = SDRLibraryExtensions.createNewArray(2 * sampleSize);
        this.middle = SDRLibraryExtensions.createNewArray(2 * sampleSize);

        plan = SDRLibraryExtensions.getFFTLibrary().createPlan(sampleSize, in, middle, true);
        plan_inverse = SDRLibraryExtensions.getFFTLibrary().createPlan(sampleSize, middle, in, false);

        this.iqLength = sampleSize * 2;
        this.fftBuffer = SDRLibraryExtensions.createNewArray(sampleSize);
    }

    private final int iqLength;
    private final IFFTPlan plan, plan_inverse;
    private final IDoubleArray in, middle;
    private final IDoubleArray fftBuffer;

    @Override
    public void processIQData(IDoubleArray iq) {
        if (iq.getLength() != iqLength) {
            throw new IllegalArgumentException();
        }

        plan.execute(iq);
        SDRLibraryExtensions.getUtilLibrary().autocorrelate1(middle);
        plan_inverse.execute();
        SDRLibraryExtensions.getUtilLibrary().scaleFFT(in);
        SDRLibraryExtensions.getUtilLibrary().autocorrelate2(in, fftBuffer, true);
    }

    @Override
    public IDoubleArray getFFTBuffer() {
        return fftBuffer;
    }

}
