package nl.elec332.sdr.lib.api;

/**
 * Created by Elec332 on 29-3-2020
 * <p>
 * A module has to provide this interface in its module-info file for it to be detected
 */
public interface ISDRExtensionProvider {

    /**
     * The first method in this interface that will be called
     * Use this method to register extensions to the SDR Library,
     * an optional default Java implementation can be passed as well
     *
     * @param library The SDR Library
     */
    void registerExtensions(IExtensionManager library);

    /**
     * This method gets called after all extensions have been registered
     * Use this to register custom implementations of different extensions
     *
     * @param library The SDR Library
     */
    void registerExtensionImplementations(IExtensionManager library);

    /**
     * Gets called after all extensions and implementations have been registered
     * When this gets called all registries will have been frozen, and
     * the getters in the registry will be available for use
     *
     * @param library The SDR Library
     */
    void afterRegister(IExtensionManager library);

}
