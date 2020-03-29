package nl.elec332.sdr.lib.ui;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * Created by Elec332 on 23-1-2020
 */
public class FFTPlot extends AbstractPlot {

    public FFTPlot(int sampleRate, double centerFreq) {
        this.bandwidth = sampleRate;
        this.leftFreq = centerFreq - (bandwidth / 2);
        format = new DecimalFormat("#.## KHz");
        this.dataPoints = new double[sampleRate];
    }

    private final double leftFreq, bandwidth;
    private final DecimalFormat format;
    private final Object LOCK = new Object();
    private double widthStep;
    private double[] dataPoints;

    public void setData(double[] data) {
        synchronized (LOCK) {
            this.dataPoints = data;
        }
    }

    @Override
    public String getHorizontalLabel(double percentage) {
        return format.format((leftFreq + bandwidth * percentage) / 1000);
    }

    @Override //-160
    public double min() {
        return -50;
    }

    @Override //10
    public double max() {
        return 20;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawHorizontal(g, y + height);
        drawVertical(g, x);

        drawAdditionalHorizontal(g, 10, Color.WHITE);

        synchronized (LOCK) {

            final int dpLength = dataPoints.length;
            if (dpLength == 0) {
                return;
            }

            widthStep = (double) width / dataPoints.length;
            double lastDataPoint = dataPoints[dpLength / 2];
            int index = 0;
            for (int i = dpLength / 2 + 1; i < dpLength; i++) {
                index++;
                double data = dataPoints[i];
                drawPart(g, index, data, lastDataPoint);
                lastDataPoint = data;
            }
            for (int i = 0; i < dpLength / 2; i++) {
                index++;
                double data = dataPoints[i];
                drawPart(g, index, data, lastDataPoint);
                lastDataPoint = data;
            }

        }

    }

    private void drawPart(Graphics g, int index, double data, double lastDataPoint) {
        double lastX = index * widthStep;
        drawDataLine(g, data, lastDataPoint, lastX, dataScale, widthStep);
    }

}
