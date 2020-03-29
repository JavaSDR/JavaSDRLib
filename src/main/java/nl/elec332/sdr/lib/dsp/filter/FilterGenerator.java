package nl.elec332.sdr.lib.dsp.filter;

/**
 * Created by Elec332 on 18-2-2020
 */
public strictfp class FilterGenerator {

    public static double[] makeRaiseCosine(double sampleRate, double freq, double alpha, int len) {
        int lm1 = len - 1;
        double[] coeffs = new double[len];
        double tim = freq / sampleRate;

        double sumofsquares = 0;
        double[] tempCoeffs = new double[len];
        int limit = (int) (0.5 / (alpha * tim));
        for (int i = 0; i <= lm1; i++) {
            double sinc = (Math.sin(2 * Math.PI * tim * (i - lm1 / 2d))) / (i - lm1 / 2d);
            double cos = Math.cos(alpha * Math.PI * tim * (i - lm1 / 2d)) / (1 - (Math.pow((2 * alpha * tim * (i - lm1 / 2d)), 2)));

            if (i == lm1 / 2) {
                tempCoeffs[i] = 2 * Math.PI * tim * cos;
            } else {
                tempCoeffs[i] = sinc * cos;
            }

            if ((i - lm1 / 2) == limit || (i - lm1 / 2) == -limit) {
                tempCoeffs[i] = 0.25 * Math.PI * sinc;
            }

            sumofsquares += tempCoeffs[i] * tempCoeffs[i];
        }
        double gain = Math.sqrt(sumofsquares);
        for (int i = 0; i < tempCoeffs.length; i++) {
            coeffs[i] = tempCoeffs[tempCoeffs.length - i - 1] / gain;
        }
        return coeffs;
    }

}
