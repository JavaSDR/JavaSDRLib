package nl.elec332.sdr.lib.api.util;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;

/**
 * Created by Elec332 on 23-1-2020
 */
public interface ISoundInput {

    AudioFormat getAudioFormat();

    void read(byte[] buffer) throws IOException;

    boolean hasReachedEnd() throws IOException;

    void close();

}
