package nl.elec332.sdr.lib.datastream;

import nl.elec332.sdr.lib.api.datastream.IDataSource;
import nl.elec332.sdr.lib.api.datastream.IPipeline;
import nl.elec332.sdr.lib.api.source.IInputSource;
import nl.elec332.sdr.lib.datastream.impl.PipelineImpl;
import nl.elec332.sdr.lib.datastream.input.InputSourceConverter;

/**
 * Created by Elec332 on 7-2-2020
 */
public class PipelineHelper {

    public static IPipeline createPipeline(IInputSource<?> source) {
        return createPipeline(new InputSourceConverter<>(source));
    }

    public static IPipeline createPipeline(IDataSource source) {
        return new PipelineImpl(source);
    }

}
