package nl.elec332.sdr.lib.extensions.filter;

import nl.elec332.sdr.lib.api.ISDRExtensionProvider;
import nl.elec332.sdr.lib.api.ISDRLibrary;
import nl.elec332.sdr.lib.api.ImplementationType;
import nl.elec332.sdr.lib.api.extensions.IFilterLibrary;
import org.bytedeco.javacpp.Loader;

/**
 * Created by Elec332 on 29-3-2020
 */
public class FilterExtensionProvider implements ISDRExtensionProvider {

    @Override
    public void registerExtensions(ISDRLibrary library) {
        library.addLibraryExtension(IFilterLibrary.class, new JavaFilterLibrary());
    }

    @Override
    public void registerExtensionImplementations(ISDRLibrary library) {
        try {
            Loader.load(NativeFilterLibrary.class);
            library.registerLibraryImplementation(IFilterLibrary.class, new NativeFilterLibrary(), ImplementationType.NATIVE);
        } catch (Exception e) {
            System.out.println("Failed to load native Filter implementation.");
        }
    }

    @Override
    public void afterRegister(ISDRLibrary library) {
    }

}
