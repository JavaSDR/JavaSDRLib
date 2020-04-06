package nl.elec332.sdr.lib;

import nl.elec332.sdr.lib.api.IExtensionManager;
import nl.elec332.sdr.lib.api.ISDRLibrary;
import nl.elec332.sdr.lib.api.SDRLibraryAPI;
import nl.elec332.sdr.lib.api.datastream.IDataSource;
import nl.elec332.sdr.lib.api.datastream.IPipeline;
import nl.elec332.sdr.lib.api.source.IInputSource;
import nl.elec332.sdr.lib.api.util.IDataConverterFactory;
import nl.elec332.sdr.lib.datastream.PipelineHelper;
import nl.elec332.sdr.lib.source.CachedDataConverterFactory;
import nl.elec332.sdr.lib.source.DynamicDataConverterFactory;

import java.lang.reflect.Field;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 5-4-2020
 */
public class SDRLibrary implements ISDRLibrary {

    private SDRLibrary() {
        if (instance != null) {
            throw new UnsupportedOperationException();
        }
    }

    private static SDRLibrary instance;

    public static SDRLibrary getInstance() {
        load();
        return instance;
    }

    public static void load() {
        if (instance == null) {
            instance = new SDRLibrary();
            ExtensionManager.load();
        }
    }

    @Override
    public IExtensionManager getExtensionManager() {
        return ExtensionManager.INSTANCE;
    }

    @Override
    public IDataConverterFactory getCachedDataConverterFactory() {
        return CachedDataConverterFactory.INSTANCE;
    }

    @Override
    public IDataConverterFactory getDynamicDataConverterFactory() {
        return DynamicDataConverterFactory.INSTANCE;
    }

    @Override
    public IPipeline createPipeline(IInputSource<?> source) {
        return PipelineHelper.createPipeline(source);
    }

    @Override
    public IPipeline createPipeline(IDataSource source) {
        return PipelineHelper.createPipeline(source);
    }

    static {
        try {
            Field f = SDRLibraryAPI.class.getDeclaredField("implGetter");
            f.setAccessible(true);
            f.set(null, (Supplier<ISDRLibrary>) SDRLibrary::getInstance);
        } catch (Exception e) {
            System.out.println("Failed to inject API");
        }
    }

}
