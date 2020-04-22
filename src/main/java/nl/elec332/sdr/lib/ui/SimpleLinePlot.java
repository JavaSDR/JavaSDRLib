package nl.elec332.sdr.lib.ui;

import java.util.Objects;
import java.util.function.DoubleFunction;

/**
 * Created by Elec332 on 21-1-2020
 */
public class SimpleLinePlot extends AbstractLinePlot {

    public SimpleLinePlot(double min, double max, DoubleFunction<String> labelMaker) {
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);
        this.labelMaker = labelMaker;
        this.data = new double[0];
    }

    private final double min, max;
    private final DoubleFunction<String> labelMaker;
    private double[] data;

    public void setData(double[] data, int highestIndex) {
        this.highestIndex = highestIndex;
        setData(data);
    }

    public void setData(double[] data) {
        this.data = Objects.requireNonNull(data);
    }

    @Override
    public double[] getDataPoints() {
        return this.data;
    }

    @Override
    public String getHorizontalLabel(double percentage) {
        return labelMaker.apply(percentage);
    }

    @Override
    public double min() {
        return this.min;
    }

    @Override
    public double max() {
        return this.max;
    }

}
