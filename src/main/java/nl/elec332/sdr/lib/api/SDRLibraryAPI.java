package nl.elec332.sdr.lib.api;

import nl.elec332.sdr.lib.api.datastream.IDataSource;
import nl.elec332.sdr.lib.api.datastream.IPipeline;
import nl.elec332.sdr.lib.api.source.IInputSource;
import nl.elec332.sdr.lib.api.util.IDataConverterFactory;

import java.util.function.Supplier;

/**
 * Created by Elec332 on 5-4-2020
 */
public class SDRLibraryAPI {

    public static ISDRLibrary getSDRLibrary() {
        if (impl == null) {
            try {
                Class.forName("nl.elec332.sdr.lib.SDRLibrary");
            } catch (Exception e) {
                System.out.println("Failed to load SDRLibrary implementation");
            }
            impl = implGetter.get();
        }
        return impl;
    }

    private static ISDRLibrary impl;
    private static Supplier<ISDRLibrary> implGetter;

    static {
        implGetter = () -> new ISDRLibrary() {

            @Override
            public IExtensionManager getExtensionManager() {
                throw new UnsupportedOperationException();
            }

            @Override
            public ISourceManager getSourceManager() {
                throw new UnsupportedOperationException();
            }

            @Override
            public IDataConverterFactory getCachedDataConverterFactory() {
                throw new UnsupportedOperationException();
            }

            @Override
            public IDataConverterFactory getDynamicDataConverterFactory() {
                throw new UnsupportedOperationException();
            }

            @Override
            public IPipeline createPipeline(IInputSource<?> source) {
                throw new UnsupportedOperationException();
            }

            @Override
            public IPipeline createPipeline(IDataSource source) {
                throw new UnsupportedOperationException();
            }

        };
    }

}
