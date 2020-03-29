package nl.elec332.sdr.lib.extensions.util;

import nl.elec332.sdr.lib.api.extensions.ISDRUtilLibrary;
import nl.elec332.sdr.lib.api.util.IDoubleArray;

import java.util.Random;

/**
 * Created by Elec332 on 7-2-2020
 */
class JavaSDRLibrary implements ISDRUtilLibrary {

    JavaSDRLibrary() {
        this.random = new Random();
    }

    private final Random random;

    @Override
    public void autocorrelate1(IDoubleArray in) {
        double[] data = in.getBuffer();
        int length = data.length;

        for (int c = 0; c < length; c += 2) {
            double i = data[c];
            double q = data[c + 1];
            data[c] = Math.sqrt(i * i + q * q);
            data[c + 1] = 0;
        }
    }

    @Override
    public void autocorrelate2(IDoubleArray iq_, IDoubleArray out_, boolean log) {
        double[] iq = iq_.getBuffer();
        double[] out = out_.getBuffer();
        int samples = out.length;

        for (int i = 0; i < samples; i++) {
            double re = iq[2 * i];
            double im = iq[2 * i + 1];
            double ret = Math.sqrt(re * re + im * im);
            if (log) {
                ret = 10 * Math.log10(ret);
            }
            out[i] = ret;
        }
    }

    @Override
    public void scaleFFT(IDoubleArray iq_) {
        double[] iq = iq_.getBuffer();
        int samples = iq.length;

        double mul = 1.0 / ((double) samples / 2);
        for (int i = 0; i < samples; i++) {
            iq[i * 2] *= mul;
            iq[i * 2 + 1] *= mul;
        }
    }

    @Override
    public void am(IDoubleArray iq_, IDoubleArray am_) {
        double[] iq = iq_.getBuffer();
        double[] am = am_.getBuffer();
        int samples = am.length;

        for (int c = 0; c < samples; c++) {
            double i = iq[2 * c];
            double q = iq[2 * c + 1];
            am[c] = Math.sqrt(i * i + q * q);
        }
    }

    @Override
    public void noise(IDoubleArray data_, int div) {
        double[] data = data_.getBuffer();
        int samples = data.length;

        for (int c = 0; c < samples; c++) {
            data[c] *= (random.nextDouble() * 2.0 - 1.0) / div;
        }
    }

    @Override
    public void resample(IDoubleArray data, int length, IDoubleArray resize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void average(IDoubleArray newData, double newInfluence, IDoubleArray data, int length) {
        throw new UnsupportedOperationException();
    }

}
