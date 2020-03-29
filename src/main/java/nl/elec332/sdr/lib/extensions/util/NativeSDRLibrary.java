package nl.elec332.sdr.lib.extensions.util;

import nl.elec332.sdr.lib.api.extensions.ISDRUtilLibrary;
import nl.elec332.sdr.lib.api.util.IDoubleArray;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.annotation.Platform;

/**
 * Created by Elec332 on 20-1-2020
 */
@Platform(include = "sdr_library.h", link = {"sdr_library"})
class NativeSDRLibrary implements ISDRUtilLibrary {

    @Override
    public void autocorrelate1(IDoubleArray in) {
        autocorrelate1(in.getPointer(), in.getLength());
    }

    @Override
    public void autocorrelate2(IDoubleArray iq, IDoubleArray out, boolean log) {
        int len = out.getLength();
        if (len != iq.getLength() / 2) {
            throw new IllegalArgumentException();
        }
        autocorrelate2(iq.getPointer(), out.getPointer(), len, log);
    }

    @Override
    public void scaleFFT(IDoubleArray iq) {
        scaleFFT(iq.getPointer(), iq.getLength() / 2);
    }

    @Override
    public void am(IDoubleArray iq, IDoubleArray am) {
        int len = am.getLength();
        if (len != iq.getLength() / 2) {
            throw new IllegalArgumentException();
        }
        am(iq.getPointer(), am.getPointer(), len);
    }

    @Override
    public void noise(IDoubleArray data, int div) {
        noise(data.getPointer(), data.getLength(), div);
    }

    @Override
    public void resample(IDoubleArray data, int length, IDoubleArray resize) {
        resample(data.getPointer(), length, resize.getPointer(), resize.getLength());
    }

    @Override
    public void average(IDoubleArray newData, double newInfluence, IDoubleArray data, int length) {
        average(newData.getPointer(), newInfluence, data.getPointer(), length);
    }

    static native void autocorrelate1(DoublePointer in, int length);

    static native void autocorrelate2(DoublePointer iq, DoublePointer out, int samples, boolean log);

    static native void scaleFFT(DoublePointer iq, int samples);

    static native void am(DoublePointer iq, DoublePointer am, int samples);

    /**
     * Adds random noise
     *
     * @param data   The samples that the noise must be applied to
     * @param length The length of the data array
     * @param div    The noise value (between -1 and 1) will be divided by this number
     */
    static native void noise(DoublePointer data, int length, int div);

    static native void resample(DoublePointer data, int len1, DoublePointer resize, int len2);

    static native void average(DoublePointer newData, double newInfluence, DoublePointer data, int length);

}

