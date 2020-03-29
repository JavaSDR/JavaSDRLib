package nl.elec332.sdr.lib.api.datastream;

import nl.elec332.sdr.lib.api.util.IDoubleArray;

/**
 * Created by Elec332 on 7-2-2020
 */
public interface ISampleDataSetter {

    void setCenterFrequency(long freq);

    void setSampleSize(int samples);

    IDoubleArray getData();

}
