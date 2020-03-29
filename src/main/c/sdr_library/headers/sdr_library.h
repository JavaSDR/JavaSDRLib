/**
 * Created by Elec332 on 29-1-2020
 */
#ifndef SDR_LIB
#define SDR_LIB

#include <common_sdr_lib.h>

#ifdef __cplusplus
extern "C" {
#endif

    EXPORT void autocorrelate1(double *data, int length);

    EXPORT void autocorrelate2(double *iq, double *out, int samples, bool log);

    EXPORT void scaleFFT(double *iq, int samples);

    EXPORT void am(double *iq, double *am, int samples);

    EXPORT void noise(double *data, int samples, int div);

    EXPORT void resample(double *data, int len, double *resample, int len2);

    EXPORT void average(double *newData, double newInfluence, double *data, int len);

#ifdef __cplusplus
}
#endif
#endif