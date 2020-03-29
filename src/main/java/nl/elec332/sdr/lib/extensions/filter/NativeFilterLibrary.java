package nl.elec332.sdr.lib.extensions.filter;

import nl.elec332.sdr.lib.api.extensions.IFilterLibrary;
import nl.elec332.sdr.lib.api.filter.IFilter;
import nl.elec332.sdr.lib.api.util.IDoubleArray;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Name;
import org.bytedeco.javacpp.annotation.Opaque;
import org.bytedeco.javacpp.annotation.Platform;

/**
 * Created by Elec332 on 18-2-2020
 */
@Platform(include = "filter_library.h", link = {"filter_library"})
class NativeFilterLibrary implements IFilterLibrary {

    @Override
    public IFilter createFilter(double[] filter) {
        return NativeFilterLibrary.createFilter(filter, filter.length);
    }

    @Opaque
    @Name("filter_object")
    private static class NatFilImpl extends Pointer implements IFilter {

        @Override
        public double filter(double value) {
            return NativeFilterLibrary.filterValue(this, value);
        }

        @Override
        public void filter(IDoubleArray data, int length) {
            NativeFilterLibrary.filterArray(this, data.getPointer(), length);
        }

        @Override
        public int getFilterLength() {
            return NativeFilterLibrary.getFilterLength(this);
        }

        @Override
        public double[] getFilter() {
            DoublePointer p = getFilterArray(this);
            double[] ret = new double[getFilterLength()];
            p.get(ret);
            return ret;
        }

    }

    private static native NatFilImpl createFilter(double[] filter, int length);

    private static native double filterValue(NatFilImpl filter, double value);

    private static native void filterArray(NatFilImpl filter, DoublePointer data, int length);

    private static native int getFilterLength(NatFilImpl filter);

    private static native DoublePointer getFilterArray(NatFilImpl filter);

}
