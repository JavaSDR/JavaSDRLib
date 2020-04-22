package nl.elec332.sdr.lib.api.source;

import nl.elec332.sdr.lib.api.util.FailedToOpenDeviceException;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 19-3-2020
 */
public interface IInputHandler<T extends IInputSource<?>> extends Supplier<T> {

    /**
     * Returns the name of this device
     *
     * @return The name of this device
     */
    @Nonnull
    String getDisplayString();

    /**
     * Returns a unique identifier that can be used to
     * easily identify this handler
     *
     * @return The identifier of this handler
     */
    @Nonnull
    String getIdentifier();

    /**
     * Creates a new device with the info from the interface
     * However, it is possible to call this function without using the interface,
     * so make sure that it works for both situations!
     * <p>
     * This function cannot return null, it will throw an exception instead
     *
     * @return a new, configured, device
     * @throws FailedToOpenDeviceException When the device failed to open
     * @throws IllegalStateException       When there is a device still active for this handler
     */
    @Nonnull
    T createDevice() throws FailedToOpenDeviceException, IllegalStateException;

    /**
     * Stops the device that is currently active,
     * does nothing when no device is active
     */
    default void stopCurrentDevice() {
        getCurrentDevice().ifPresent(IInputSource::stop);
    }

    /**
     * Returns the device that is currently active
     * Can be null if no device has been created yet
     *
     * @return The device that is currently active
     */
    @Nonnull
    Optional<T> getCurrentDevice();

    /**
     * Creates a configuration panel.
     * This UI allows for easy configuration by the user
     *
     * @param panel The base panel, may have size restrictions
     * @throws UnsupportedOperationException When this handler has no configuration UI
     * @throws IllegalStateException         When there is a UI already active for this handler
     */
    default void createInterface(JPanel panel) throws UnsupportedOperationException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the current device or creates a new one
     */
    @Nonnull
    @SuppressWarnings("all")
    default T get() {
        return getCurrentDevice().orElseGet(() -> {
            T ret = createDevice();
            if (ret == null) {
                throw new FailedToOpenDeviceException(new NullPointerException());
            }
            return ret;
        });
    }

}
