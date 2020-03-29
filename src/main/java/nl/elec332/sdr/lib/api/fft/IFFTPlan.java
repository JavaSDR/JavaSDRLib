package nl.elec332.sdr.lib.api.fft;

import nl.elec332.sdr.lib.api.util.IDoubleArray;

/**
 * Created by Elec332 on 7-2-2020
 */
public interface IFFTPlan {

    void execute(IDoubleArray in);

    void execute();

}
