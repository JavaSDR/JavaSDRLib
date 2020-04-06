package nl.elec332.sdr.lib.extensions;

import nl.elec332.sdr.lib.SDRLibrary;
import nl.elec332.sdr.lib.api.extensions.IFFTLibrary;
import nl.elec332.sdr.lib.api.extensions.IFilterLibrary;
import nl.elec332.sdr.lib.api.extensions.ISDRUtilLibrary;
import nl.elec332.sdr.lib.api.util.IDoubleArray;
import nl.elec332.sdr.lib.api.util.IFiFoDoubleBuffer;

/**
 * Created by Elec332 on 29-3-2020
 */
public class SDRLibraryExtensions {

    public static ISDRUtilLibrary getUtilLibrary() {
        return DelegatedSDRLibraryExtensions.UTIL_LIB;
    }

    public static IFFTLibrary getFFTLibrary() {
        return DelegatedSDRLibraryExtensions.FFT_LIB;
    }

    public static IFilterLibrary getFilterLibrary() {
        return DelegatedSDRLibraryExtensions.FILTER_LIB;
    }

    public static boolean isNativePresent() {
        return DelegatedSDRLibraryExtensions.nativePresent;
    }

    public static IDoubleArray createNewArray(int size) {
        return isNativePresent() ? new NativeDoubleArray(size) : new JavaDoubleArray(size);
    }

    public static IDoubleArray createNewArray(double[] data) {
        return isNativePresent() ? new NativeDoubleArray(data) : new JavaDoubleArray(data);
    }

    public static IFiFoDoubleBuffer createNewBuffer(int maxSize) {
        return isNativePresent() ? new NativeFiFoDoubleRingBuffer(maxSize) : createNewJavaBuffer(maxSize);
    }

    public static IFiFoDoubleBuffer createNewJavaBuffer(int maxSize) {
        return new JavaFiFoDoubleRingBuffer(maxSize);
    }

    private static class DelegatedSDRLibraryExtensions {

        protected static final ISDRUtilLibrary UTIL_LIB;
        protected static final IFFTLibrary FFT_LIB;
        protected static final IFilterLibrary FILTER_LIB;

        protected static final boolean nativePresent;

        static {
            UTIL_LIB = SDRLibrary.getInstance().getExtensionManager().getBestImplementation(ISDRUtilLibrary.class);
            FFT_LIB = SDRLibrary.getInstance().getExtensionManager().getBestImplementation(IFFTLibrary.class);
            FILTER_LIB = SDRLibrary.getInstance().getExtensionManager().getBestImplementation(IFilterLibrary.class);
            nativePresent = SDRLibrary.getInstance().getExtensionManager().nativeImplementationsPresent();
        }

    }

}
