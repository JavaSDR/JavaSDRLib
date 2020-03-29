package nl.elec332.sdr.lib.source.waviq;

import nl.elec332.lib.java.swing.FileChooserHelper;
import nl.elec332.lib.java.swing.LinedGridBagConstraints;
import nl.elec332.sdr.lib.api.util.FailedToOpenDeviceException;
import nl.elec332.sdr.lib.api.util.ISoundInput;
import nl.elec332.sdr.lib.source.AbstractInputHandler;
import nl.elec332.sdr.lib.util.audio.FileAudioSource;

import javax.annotation.Nonnull;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * Created by Elec332 on 22-3-2020
 */
public class AudioInputHandler extends AbstractInputHandler<IQAudioSource, byte[]> {

    public AudioInputHandler() {
        this.sampPB = 1024 * 128;
        this.file = null;
        this.format = null;
    }

    private static final FileNameExtensionFilter filter = new FileNameExtensionFilter("Audio file (*.wav)", "wav");

    private int sampPB;
    private File file;
    private AudioFormat format;

    @Nonnull
    @Override
    public String getDisplayString() {
        return "IQ Sample File (.wav)";
    }

    @Override
    protected boolean createNewInterface(JPanel panel) {
        panel.setLayout(new GridBagLayout());

        JPanel line1 = new JPanel();
        line1.add(new JLabel("File name: "));
        JLabel fileName = new JLabel("----------");
        line1.add(fileName);
        panel.add(line1, new LinedGridBagConstraints(0));

        panel.add(new JPanel(), new LinedGridBagConstraints(1));

        JPanel line2 = new JPanel();
        line2.add(new JLabel("Sample rate: "));
        JLabel sr = new JLabel("------");
        line2.add(sr);
        line2.add(new JLabel("   Resolution: "));
        JLabel res = new JLabel("--");
        line2.add(res);
        line2.add(new JLabel(" bits"));
        panel.add(line2, new LinedGridBagConstraints(2));

        panel.add(new JPanel(), new LinedGridBagConstraints(3));

        JPanel line3 = new JPanel();
        JButton chf = new JButton("Change file");
        chf.addActionListener(a -> {
            this.file = FileChooserHelper.openFileChooser(panel, filter, this.file, "Choose sample file");
            getCurrentDevice().ifPresent(IQAudioSource::close);
            fileName.setText(file == null ? "----------" : file.getName());
            try {
                AudioInputStream ais = AudioSystem.getAudioInputStream(file);
                format = ais.getFormat();
                ais.close();
            } catch (Exception e) {
                format = null;
            }
            if (format == null) {
                sr.setText("<unknown>");
                res.setText("<?>");
            } else {
                float s = format.getSampleRate();
                String txt = "SPS";
                if (s > 1000000) {
                    s /= 1000000;
                    txt = "MSPS";
                } else if (s > 1000) {
                    s /= 1000;
                    txt = "KSPS";
                }
                sr.setText(String.format("%.2f %s", s, txt));
                res.setText(format.getSampleSizeInBits() + "");
            }
        });

        line3.add(chf);
        listeners.add(chf);
        panel.add(line3, new LinedGridBagConstraints(4));
        return true;
    }

    @Override
    protected IQAudioSource createNewDevice() {
        if (file == null) {
            throw new FailedToOpenDeviceException("No file has been selected!");
        }
        if (format == null) {
            throw new FailedToOpenDeviceException("File could not be opened properly");
        }
        ISoundInput sound;
        try {
            sound = new FileAudioSource(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new IQAudioSource(sound, sampPB);
    }

    @Override
    protected boolean isDeviceValid(IQAudioSource device) {
        if (device.hasReachedEnd()) {
            device.close();
            return false;
        }
        return true;
    }

}
