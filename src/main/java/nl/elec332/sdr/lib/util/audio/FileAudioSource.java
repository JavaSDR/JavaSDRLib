package nl.elec332.sdr.lib.util.audio;

import nl.elec332.sdr.lib.api.util.ISoundInput;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Created by Elec332 on 23-1-2020
 */
public class FileAudioSource implements ISoundInput {

    public FileAudioSource(File file) throws IOException, UnsupportedAudioFileException {
        this.ais = AudioSystem.getAudioInputStream(file);
    }

    private final AudioInputStream ais;

    @Override
    public AudioFormat getAudioFormat() {
        return ais.getFormat();
    }

    @Override
    @SuppressWarnings("all")
    public void read(byte[] buffer) throws IOException {
        ais.read(buffer);
    }

    @Override
    public boolean hasReachedEnd() throws IOException {
        return !(ais.available() > 3);
    }

    @Override
    public void close() {
        try {
            ais.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
