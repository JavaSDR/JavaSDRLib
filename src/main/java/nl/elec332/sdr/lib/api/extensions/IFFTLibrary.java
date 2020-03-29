package nl.elec332.sdr.lib.api.extensions;

import nl.elec332.sdr.lib.api.fft.IFFTPlan;
import nl.elec332.sdr.lib.api.util.IDoubleArray;

/**
 * Created by Elec332 on 7-2-2020
 */
public interface IFFTLibrary {

    IFFTPlan createPlan(int sampleSize, IDoubleArray out, boolean forward);

    IFFTPlan createPlan(int sampleSize, IDoubleArray in, IDoubleArray out, boolean forward);

}
