/**
 * Created by Elec332 on 18-2-2020
 */
#ifndef SDRFILTER_LIB
#define SDRFILTER_LIB

#include <common_sdr_lib.h>

typedef struct {

    int length;
    double* filter;
    double* buffer;
    int bufferIndex;

} filter_object;

#ifdef __cplusplus
extern "C" {
#endif

    EXPORT filter_object* createFilter(double* filter, int len);

    EXPORT double filterValue(filter_object* filter, double in);

    EXPORT void filterArray(filter_object* filter, double* in, int len);

    EXPORT int getFilterLength(filter_object* filter);

    EXPORT double* getFilterArray(filter_object* filter);

#ifdef __cplusplus
}
#endif
#endif