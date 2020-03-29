package nl.elec332.sdr.lib.api;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Created by Elec332 on 29-3-2020
 */
public interface ISDRLibrary {

    <T> void addLibraryExtension(Class<T> extension, T defaultImpl);

    <T> void registerLibraryImplementation(Class<T> extension, T impl, ImplementationType type);

    <T> boolean isExtensionRegistered(Class<T> extension);

    <T> boolean hasImplementationType(Class<T> extension, ImplementationType type);

    default <T> ImplementationType getBestImplementationType(Class<T> extension) {
        ImplementationType[] types = ImplementationType.values();
        for (int i = types.length - 1; i >= 0; i--) {
            ImplementationType t = types[i];
            if (hasImplementationType(extension, t)) {
                return t;
            }
        }
        throw new IllegalStateException("No (default) implementation found for: " + extension.getCanonicalName());
    }

    @Nullable
    <T> T getImplementation(Class<T> extension, ImplementationType type);

    <T> T getBestImplementation(Class<T> extension);

    Set<Class<?>> getExtensionTypes();

    boolean nativeImplementationsPresent();

}
