/**
 * Created by Elec332 on 20-1-2020
 */
#include <time.h>
#include <sdr_library.h>

EXPORT void autocorrelate1(double *data, int length) {
    for (int c = 0; c < length; c += 2) {
        double i = data[c];
        double q = data[c + 1];
        data[c] = sqrt(i * i + q * q);
        data[c + 1] = 0;
    }
}

EXPORT void autocorrelate2(double *iq, double *out, int samples, bool log) {
    for (int i = 0; i < samples; i++) {
        double re = iq[2 * i];
        double im = iq[2 * i + 1];
        double ret = sqrt(re * re + im * im);
        if (log) {
            ret = 10 * log10(ret);
        }
        out[i] = ret;
    }
}

EXPORT void scaleFFT(double *iq, int samples) {
    double mul = 1.0 / samples;
    for (int i = 0; i < samples; i++) {
        iq[i * 2] *= mul;
        iq[i * 2 + 1] *= mul;
    }
}

EXPORT void am(double *iq, double *am, int samples) {
    for (int c = 0; c < samples; c++) {
        double i = iq[2 * c];
        double q = iq[2 * c + 1];
        am[c] = sqrt(i * i + q * q);
    }
}

EXPORT void noise(double *data, int samples, int div) {
    srand(time(NULL));
    for(int c = 0; c < samples; c++) {
        data[c] = data[c] * (double) ((rand() / RAND_MAX * 2.0 - 1.0) / div);
    }
}

EXPORT void resample(double *data, int len, double *resample, int len2) {
    resample[0] = data[0];
    resample[len2 - 1] = data[len - 1];
    double spp = (double)(len - 1) / (double)(len2 - 1);//Samples per pixel
    for (int i = 1; i < len2 - 1; i++) {
        double jd = i * spp;
        int j = (int)jd;
        resample[i] = data[j] + (data[j + 1] - data[j]) * (jd - (double)j);
    }
}

EXPORT void average(double *newData, double newInfluence, double *data, int len) {
    double oldInf = 1.0 - newInfluence;
    for (int i = 0; i < len; i++) {
        data[i] = data[i] * oldInf + newData[i] * newInfluence;
    }
}
