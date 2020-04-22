package nl.elec332.sdr.lib.api;

import nl.elec332.sdr.lib.api.datastream.IDataSource;
import nl.elec332.sdr.lib.api.datastream.IPipeline;
import nl.elec332.sdr.lib.api.source.IInputSource;
import nl.elec332.sdr.lib.api.util.IDataConverterFactory;

/**
 * Created by Elec332 on 5-4-2020
 */
public interface ISDRLibrary {

    IExtensionManager getExtensionManager();

    ISourceManager getSourceManager();

    IDataConverterFactory getCachedDataConverterFactory();

    IDataConverterFactory getDynamicDataConverterFactory();

    IPipeline createPipeline(IInputSource<?> source);

    IPipeline createPipeline(IDataSource source);

}
