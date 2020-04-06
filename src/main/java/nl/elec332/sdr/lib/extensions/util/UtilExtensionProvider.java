package nl.elec332.sdr.lib.extensions.util;

import nl.elec332.sdr.lib.api.IExtensionManager;
import nl.elec332.sdr.lib.api.ISDRExtensionProvider;
import nl.elec332.sdr.lib.api.ImplementationType;
import nl.elec332.sdr.lib.api.extensions.ISDRUtilLibrary;
import org.bytedeco.javacpp.Loader;

/**
 * Created by Elec332 on 29-3-2020
 */
public class UtilExtensionProvider implements ISDRExtensionProvider {

    @Override
    public void registerExtensions(IExtensionManager library) {
        library.addLibraryExtension(ISDRUtilLibrary.class, new JavaSDRLibrary());
    }

    @Override
    public void registerExtensionImplementations(IExtensionManager library) {
        try {
            Loader.load(NativeSDRLibrary.class);
            library.registerLibraryImplementation(ISDRUtilLibrary.class, new NativeSDRLibrary(), ImplementationType.NATIVE);
        } catch (Exception e) {
            System.out.println("Failed to load native SDRLib implementation.");
        }
    }

    @Override
    public void afterRegister(IExtensionManager library) {
    }

}
