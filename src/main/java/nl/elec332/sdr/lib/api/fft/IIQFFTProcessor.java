package nl.elec332.sdr.lib.api.fft;

import nl.elec332.sdr.lib.api.util.IDoubleArray;

/**
 * Created by Elec332 on 31-1-2020
 */
public interface IIQFFTProcessor {

    void processIQData(IDoubleArray iq);

    IDoubleArray getFFTBuffer();

}
