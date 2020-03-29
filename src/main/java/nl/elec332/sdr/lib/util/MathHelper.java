package nl.elec332.sdr.lib.util;

import java.util.function.ToDoubleFunction;

/**
 * Created by Elec332 on 21-1-2020
 */
public class MathHelper {

    public static boolean isWithinRange(double norm, double val, double leeway) {
        double l = norm * leeway;
        return val <= norm + l && val >= norm - l;
    }

    /**
     * Normalizes the supplied IQ samples
     *
     * @param in The IQ array that needs to be normalized
     */
    public static void normalize(double[] in) {
        if (in.length != 2) {
            throw new UnsupportedOperationException();
        }
        double lenSq = in[0] * in[0] + in[1] * in[1];
        if (lenSq == 0) {
            return;
        }
        double mul = 1 / Math.sqrt(lenSq);
        in[0] *= mul;
        in[1] *= mul;
    }

    /**
     * Calculates the Power Spectral Density (PSD)
     *
     * @param re        Real part of the signal
     * @param im        Imaginary part of the signal
     * @param bandwidth The bandwidth this value covers
     * @return The PSD for the given values
     */
    public static double calculatePSD(double re, double im, double bandwidth) {
        return 20 * Math.log10(Math.sqrt(re * re + im * im) / bandwidth);
    }

    public static void map(double[] data, ToDoubleFunction<Double> map) {
        for (int i = 0; i < data.length; i++) {
            data[i] = map.applyAsDouble(data[i]);
        }
    }

    public static double[] extractMiddle(double[] data, int middle, int range) {
        return extract(data, middle - range / 2, middle + range / 2);
    }

    public static double[] extract(double[] data, int from, int to) {
        double[] ret = new double[to - from];
        int c = 0;
        for (int i = from; i < to; i++) {
            ret[c] = data[i];
            c++;
        }
        return ret;
    }

    public static double[] part(double[] input, int xTh) {
        if (xTh <= 1) {
            return input;
        }
        int size = Math.floorDiv(input.length, xTh);
        double[] ret = new double[size];
        for (int i = 0; i < size; i++) {
            ret[i] = input[i * xTh];
        }
        return ret;
    }

    public static int nextPowerOf2(final int a) {
        int b = 1;
        while (b < a) {
            b = b << 1;
        }
        return b;
    }

}
