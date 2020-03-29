package nl.elec332.sdr.lib.api.util;

import javax.sound.sampled.AudioFormat;

/**
 * Created by Elec332 on 24-3-2020
 */
public interface IDataConverterFactory {

    default IDataConverter createConverter(int bitsPerSample, boolean signed) {
        return createConverter(bitsPerSample, false, signed);
    }

    IDataConverter createConverter(int bitsPerSample, boolean bigEndian, AudioFormat.Encoding encoding);

    IDataConverter createConverter(int bitsPerSample, boolean bigEndian, boolean signed);

}
