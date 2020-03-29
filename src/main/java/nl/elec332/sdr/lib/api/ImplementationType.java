package nl.elec332.sdr.lib.api;

/**
 * Created by Elec332 on 29-3-2020
 */
public enum ImplementationType {

    JAVA_DEFAULT(false),
    JAVA(false),
    JAVA_FAST(false),
    NATIVE(true),
    NATIVE_FAST(true),
    GPU(true),
    GPU_FAST(true);

    ImplementationType(boolean isNative) {
        this.isNative = isNative;
    }

    private final boolean isNative;

    public boolean isNative() {
        return this.isNative;
    }

}
