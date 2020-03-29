package nl.elec332.sdr.lib.api.extensions;

import nl.elec332.sdr.lib.api.util.IDoubleArray;

/**
 * Created by Elec332 on 7-2-2020
 */
public interface ISDRUtilLibrary {

    void autocorrelate1(IDoubleArray in);

    void autocorrelate2(IDoubleArray iq, IDoubleArray out, boolean log);

    void scaleFFT(IDoubleArray iq);

    void am(IDoubleArray iq, IDoubleArray am);

    /**
     * Adds random noise
     *
     * @param data The samples that the noise must be applied to
     * @param div  The noise value (between -1 and 1) will be divided by this number
     */
    void noise(IDoubleArray data, int div);

    void resample(IDoubleArray data, int length, IDoubleArray resize);

    void average(IDoubleArray newData, double newInfluence, IDoubleArray data, int length);

}
