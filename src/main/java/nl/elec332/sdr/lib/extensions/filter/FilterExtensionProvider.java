package nl.elec332.sdr.lib.extensions.filter;

import nl.elec332.sdr.lib.api.IExtensionManager;
import nl.elec332.sdr.lib.api.ISDRExtensionProvider;
import nl.elec332.sdr.lib.api.ImplementationType;
import nl.elec332.sdr.lib.api.extensions.IFilterLibrary;
import org.bytedeco.javacpp.Loader;

/**
 * Created by Elec332 on 29-3-2020
 */
public class FilterExtensionProvider implements ISDRExtensionProvider {

    @Override
    public void registerExtensions(IExtensionManager library) {
        library.addLibraryExtension(IFilterLibrary.class, new JavaFilterLibrary());
    }

    @Override
    public void registerExtensionImplementations(IExtensionManager library) {
        try {
            Loader.load(NativeFilterLibrary.class);
            library.registerLibraryImplementation(IFilterLibrary.class, new NativeFilterLibrary(), ImplementationType.NATIVE);
        } catch (Exception e) {
            System.out.println("Failed to load native Filter implementation.");
        }
    }

    @Override
    public void afterRegister(IExtensionManager library) {
    }

}
