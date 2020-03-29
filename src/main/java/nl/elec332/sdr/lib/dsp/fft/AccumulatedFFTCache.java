package nl.elec332.sdr.lib.dsp.fft;

import nl.elec332.sdr.lib.api.datastream.IPipeline;
import nl.elec332.sdr.lib.api.datastream.ISampleData;
import nl.elec332.sdr.lib.api.fft.IIQFFTProcessor;
import nl.elec332.sdr.lib.api.util.IFiFoDoubleBuffer;
import nl.elec332.sdr.lib.extensions.SDRLibraryExtensions;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Elec332 on 13-2-2020
 */
public class AccumulatedFFTCache {

    public AccumulatedFFTCache(int samples) {
        this.samples = samples;
        System.out.println("NEWIQFFT");
        this.fft = new IQFFT(samples);
        this.buffer = SDRLibraryExtensions.createNewBuffer(samples * 100);
        this.listener = dummy -> {
        };
    }

    private final int samples;
    private final IIQFFTProcessor fft;
    private final IFiFoDoubleBuffer buffer;

    private Consumer<double[]> listener;
    private IPipeline pipeline;

    public Function<IPipeline, Consumer<ISampleData>> listen(Consumer<ISampleData> end) {
        return pipeline -> {
            if (this.pipeline != null) {
                throw new IllegalStateException();
            }
            Consumer<ISampleData> rend = data -> {
                buffer.accept(data);
                end.accept(data);
            };
            this.pipeline = pipeline;
            start();
            return rend;
        };
    }

    public void addListener(Consumer<double[]> listener) {
        this.listener = this.listener.andThen(listener);
    }

    private void start() {
        IPipeline myPipeline = buffer.createOutput(samples * 2, pipeline.getSampleRate())
                .end(data -> {
                    fft.processIQData(data.getData());
                    this.listener.accept(fft.getFFTBuffer().getBuffer());
                });
        myPipeline.start();
        pipeline.addChild(myPipeline);
    }

}
