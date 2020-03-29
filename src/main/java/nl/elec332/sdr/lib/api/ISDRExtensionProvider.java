package nl.elec332.sdr.lib.api;

/**
 * Created by Elec332 on 29-3-2020
 * <p>
 * For classes implementing this to be automatically detected:
 * Make sure the class-name contains 2 or more of the following elements:
 * "SDR", "Provider", "Impl" and "Extension"
 */
public interface ISDRExtensionProvider {

    /**
     * The first method in this interface that will be called
     * Use this method to register extensions to the SDR Library,
     * an optional default Java implementation can be passed as well
     *
     * @param library The SDR Library
     */
    void registerExtensions(ISDRLibrary library);

    /**
     * This method gets called after all extensions have been registered
     * Use this to register custom implementations of different extensions
     *
     * @param library The SDR Library
     */
    void registerExtensionImplementations(ISDRLibrary library);

    /**
     * Gets called after all extensions and implementations have been registered
     * When this gets called all registries will have been frozen, and
     * the getters in the registry will be available for use
     *
     * @param library The SDR Library
     */
    void afterRegister(ISDRLibrary library);

}
