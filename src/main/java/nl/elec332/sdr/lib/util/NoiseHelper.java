package nl.elec332.sdr.lib.util;

import nl.elec332.sdr.lib.api.datastream.ISampleData;
import nl.elec332.sdr.lib.api.util.IDoubleArray;
import nl.elec332.sdr.lib.extensions.SDRLibraryExtensions;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 21-1-2020
 */
public class NoiseHelper {

    public static void addNoise(IDoubleArray values, int noiseFactor) {
        SDRLibraryExtensions.getUtilLibrary().noise(values, noiseFactor);
    }

    public static Consumer<ISampleData> addNoise(final int noiseFactor) {
        return sampleData -> addNoise(sampleData.getData(), noiseFactor);
    }

}
