package nl.elec332.sdr.lib.util;

import nl.elec332.sdr.lib.ui.FFTPlot;
import nl.elec332.sdr.lib.ui.SimpleLinePlot;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 21-1-2020
 * <p>
 * Used for easy debugging, otherwise useless
 */
public class SwingHelper {

    private static final DecimalFormat format = new DecimalFormat("#.##");

    public static String formatDouble(double d) {
        return format.format(d);
    }

    public static Consumer<double[]> openSimplePlotWindow(double min, double max, int sampleRate) {
        SimpleLinePlot linePlot = openSimplePlotWindow(min, max, percentage -> formatDouble(percentage * sampleRate / 1000) + " KHz");
        return linePlot::setData;
    }

    public static SimpleLinePlot openSimplePlotWindow(double min, double max, DoubleFunction<String> labelMaker) {
        SimpleLinePlot linePlot = new SimpleLinePlot(min, max, labelMaker);
        openWindow(f -> f.add(linePlot), "Simple plot");
        return linePlot;
    }

    public static FFTPlot openFFTWindow(int sampleRate, double centerFreq) {
        final FFTPlot linePlot = new FFTPlot(sampleRate, centerFreq);
        openWindow(f -> f.add(linePlot), "FFT");
        return linePlot;
    }

    public static JPanel showImage(Supplier<BufferedImage> img) {
        JPanel panel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                BufferedImage imge = img.get();
                g.drawImage(imge, 0, 0, 1920 / 2, 1080 / 2, this);
            }

        };
        panel.setPreferredSize(new Dimension(1920 / 2, 1080 / 2));
        openWindow(f -> f.add(panel));
        return panel;
    }

    public static void openWindow(Consumer<JFrame> builder) {
        openWindow(builder, "test window");
    }

    public static void openWindow(Consumer<JFrame> builder, String name) {
        JFrame frame = new JFrame(name);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        builder.accept(frame);
        frame.pack();
        if (frame.getSize().getHeight() < 200) {
            frame.setSize(new Dimension(800, 600));
            frame.revalidate();
        }
        frame.setVisible(true);
    }

}
