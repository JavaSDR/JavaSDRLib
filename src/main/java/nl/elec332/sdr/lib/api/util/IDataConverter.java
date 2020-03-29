package nl.elec332.sdr.lib.api.util;

import nl.elec332.sdr.lib.api.datastream.ISampleDataSetter;

/**
 * Created by Elec332 on 24-3-2020
 */
public interface IDataConverter {

    void readData(byte[] raw, ISampleDataSetter sampleData);

}
