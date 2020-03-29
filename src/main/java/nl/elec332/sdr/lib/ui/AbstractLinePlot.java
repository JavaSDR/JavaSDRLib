package nl.elec332.sdr.lib.ui;

import java.awt.*;

/**
 * Created by Elec332 on 21-1-2020
 */
public abstract class AbstractLinePlot extends AbstractPlot {

    protected int highestIndex = -1;
    private int labels = super.getHorizontalLabelCount();
    private int vLabels = super.getVerticalLabelCount();

    @Override
    public int getHorizontalLabelCount() {
        return labels;
    }

    @Override
    public int getVerticalLabelCount() {
        return vLabels;
    }

    public void setHorizontalLabelCount(int labels) {
        this.labels = labels;
    }

    public void setVerticalLabelCount(int labels) {
        this.vLabels = labels;
    }

    public abstract double[] getDataPoints();

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        double[] dataPoints = getDataPoints();

        drawHorizontal(g, y + height);
        drawVertical(g, x);

        drawAdditionalHorizontal(g, 10, Color.WHITE);

        if (dataPoints.length == 0) {
            return;
        }

        double widthStep = (double) width / dataPoints.length;

        double lastDataPoint = dataPoints[0];
        for (int i = 1; i < dataPoints.length; i++) {
            double lastX = i * widthStep;

            double data = dataPoints[i];
            drawDataLine(g, data, lastDataPoint, lastX, dataScale, widthStep);

            if (i == highestIndex) {
                Color c = g.getColor();
                g.setColor(Color.BLUE);
                g.drawLine(x + (int) lastX, y, x + (int) lastX, y + height);
                g.setColor(c);
            }

            lastDataPoint = data;
        }

    }

}
