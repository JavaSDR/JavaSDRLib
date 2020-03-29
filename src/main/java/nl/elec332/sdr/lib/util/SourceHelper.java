package nl.elec332.sdr.lib.util;

/**
 * Created by Elec332 on 19-3-2020
 * <p>
 * Im open for better class names...
 */
public class SourceHelper {

    public static int getNextHigherOptimalSampleRate(long sampleRate, int[] sampleRates) {
        for (int opt : sampleRates) {
            if (sampleRate <= opt) {
                return opt;
            }
        }
        return sampleRates[sampleRates.length - 1];
    }

}
