/**
 * Created by Elec332 on 18-2-2020
 */
#include <filter_library.h>

EXPORT filter_object* createFilter(double* filter, int len) {
    filter_object* ret;
    ret = (filter_object*)malloc(sizeof(*ret));
    ret->length = len;
    ret->filter = (double*) malloc(len * sizeof(double));
    ret->buffer = (double*) malloc(len * sizeof(double)); //Don't use the java-allocated buffer, it invalidates and causes issues...
    for (int i = 0; i < len; i++) {
        ret->buffer[i] = 0;
        ret->filter[i] = filter[i];
    }
    ret->bufferIndex = 0;
    return ret;
}

EXPORT double filterValue(filter_object* filter, double in) {
    double* buf = filter->buffer;
    double* fil = filter->filter;
    int len = filter->length;
    int bufIdx = filter->bufferIndex;
    buf[bufIdx] = in;
    bufIdx++;
    if (bufIdx >= len) {
        bufIdx= 0;
    }
    filter->bufferIndex = bufIdx;

    double ret = 0.0;
    for (int i = 0; i < len; i++) {
        ret += buf[(i + bufIdx) % len] * fil[i];
    }
    return ret;
}

EXPORT void filterArray(filter_object* filter, double* in, int len) {
    for (int i = 0; i < len; i++) {
        in[i] = filterValue(filter, in[i]);
    }
    for (int i = 0; i < filter->length; i++) {
        filter->buffer[i] = 0;
    }
}

EXPORT int getFilterLength(filter_object* filter) {
    return filter->length;
}

EXPORT double* getFilterArray(filter_object* filter) {
    return filter->filter;
}