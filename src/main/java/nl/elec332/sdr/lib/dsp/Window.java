package nl.elec332.sdr.lib.dsp;

import nl.elec332.sdr.lib.api.datastream.IPipeline;
import nl.elec332.sdr.lib.api.datastream.ISampleData;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * Created by Elec332 on 23-1-2020
 */
public class Window {

    public static Function<IPipeline, Consumer<ISampleData>> applyIQWindow(IntFunction<double[]> window) {
        return pipeline -> {
            int sampleSize = pipeline.getSampleSize() / 2;
            final double[] window_ = window.apply(sampleSize);
            return sampleData -> {
                double[] buf = sampleData.getData().getBuffer();
                for (int i = 0; i < sampleSize; i++) {
                    buf[i * 2] *= window_[i];
                    buf[i * 2 + 1] *= window_[i];
                }
            };
        };
    }

    public static double[] initBlackmanWindow(int length) {
        if (length % 2 == 0) {
            length--;
        }
        double[] blackmanWindow = new double[length + 1];
        for (int i = 0; i <= length; i++) {
            blackmanWindow[i] = (0.42 - 0.5 * Math.cos(2 * Math.PI * i / (length - 1)) + 0.08 * Math.cos((4 * Math.PI * i) / (length - 1)));
        }
        return blackmanWindow;
    }

    public static double[] initSimpleWindow(int length) {
        double[] simpleWindow = new double[length];
        for (int i = 0; i < length; i++) {
            simpleWindow[i] = 0.5 * Math.cos(2 * Math.PI * i / (length - 1));
        }
        return simpleWindow;
    }

}
