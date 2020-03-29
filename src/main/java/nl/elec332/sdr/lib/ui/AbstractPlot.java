package nl.elec332.sdr.lib.ui;

import nl.elec332.sdr.lib.util.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Elec332 on 21-1-2020
 */
public abstract class AbstractPlot extends JComponent {

    public AbstractPlot() {
        this(Constants.SWING_DRAW_BORDER, Constants.SWING_DRAW_BORDER);
    }

    public AbstractPlot(int xOffset, int yOffset) {
        x = xOffset;
        y = yOffset;
        color = Color.BLACK;
    }

    protected int x, y, width, height;
    protected double min, max, range, dataScale;
    protected Color color;

    public void setBorder(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getHorizontalLabelCount() {
        return 11;
    }

    public int getVerticalLabelCount() {
        return 18;
    }

    public abstract String getHorizontalLabel(double percentage);

    public abstract double min();

    public abstract double max();

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        height = getHeight() - x * 2;
        width = getWidth() - y * 2;
        min = min();
        max = max();
        range = max - min;
        dataScale = height / range;
        g.setColor(color);
    }

    public void drawDataLine(Graphics g, double data, double lastDataPoint, double lastX, double dataScale, double widthStep) {
        data = -(data - max);
        lastDataPoint = -(lastDataPoint - max);
        int currentX = (int) (lastX + widthStep);
        if (currentX > width) {
            return;
        }
        int currentY = (int) (data * dataScale);
        double lastY = lastDataPoint * dataScale;

        //Do not draw below and above bounds
        boolean lastInvalid = lastDataPoint > range || lastDataPoint < 0;
        if (data > range || data < 0) {
            if (lastInvalid) {
                return;
            }
            double diff = Math.abs(currentY - lastY);
            double tm = currentY;
            currentY = 0;
            if (data > 0) {
                tm -= height;
                currentY = height;
            }
            tm = Math.abs(tm);
            currentX = (int) (lastX + ((diff - tm) / diff) * widthStep);
        } else if (lastInvalid) { //Can't both be true
            double diff = Math.abs(currentY - lastY);
            double tm = lastY;
            lastY = 0;
            if (data > 0) {
                tm -= height;
                lastY = height;
            }
            tm = Math.abs(tm);
            lastX = (int) (lastX + ((diff - tm) / diff) * widthStep);
        }

        g.drawLine(x + (int) (lastX), y + (int) lastY, x + currentX, y + currentY);
    }

    public void drawAdditionalHorizontal(Graphics g, double step, Color color) {
        g.setColor(color);
        for (double j = max; j > min; j -= step) {
            g.drawLine(x, (int) (y + -(j - max) * dataScale), x + width, (int) (y + -(j - max) * dataScale));
        }
        g.setColor(this.color);
    }

    public void drawHorizontal(Graphics g, int y) {
        int labels = getHorizontalLabelCount();
        double labelStep = (double) width / (labels - 1.0);
        g.drawLine(x, y, x + width, y);
        for (int i = 0; i < labels; i++) {
            int x = (int) (i * labelStep);
            g.drawLine(this.x + x, y, this.x + x, y + 10);
            String s = getHorizontalLabel((double) x / width);
            g.drawString(s, this.x + x - s.length() * 3, y + 20);
        }
    }

    public void drawVertical(Graphics g, int x) {
        double labels = getVerticalLabelCount();
        double increments = height / (labels - 1.0);
        double valIncrements = range / (labels - 1.0);
        g.drawLine(x, y, x, y + height);
        for (int i = 0; i < labels; i++) {
            int y = (int) (this.y + i * increments);
            g.drawLine(x - 10, y, x, y);
            g.drawString("" + (int) (min + valIncrements * (labels - (i + 1))), x - 33, y + 6);
        }
    }

}
