package nl.elec332.sdr.lib.source;

import nl.elec332.sdr.lib.api.util.IDataConverter;
import nl.elec332.sdr.lib.api.util.IDataConverterFactory;

import javax.sound.sampled.AudioFormat;

/**
 * Created by Elec332 on 24-3-2020
 */
public enum DynamicDataConverterFactory implements IDataConverterFactory {

    INSTANCE;

    public IDataConverter createConverter(int bitsPerSample, boolean bigEndian, AudioFormat.Encoding encoding) {
        if (encoding != AudioFormat.Encoding.PCM_SIGNED && encoding != AudioFormat.Encoding.PCM_UNSIGNED) {
            throw new IllegalArgumentException();
        }
        return createConverter(bitsPerSample, bigEndian, encoding == AudioFormat.Encoding.PCM_SIGNED);
    }

    public IDataConverter createConverter(int bitsPerSample, boolean bigEndian, boolean signed) {
        if (bitsPerSample % 8 != 0 || bitsPerSample > 56) {
            throw new IllegalArgumentException();
        }
        final int bytes = bitsPerSample / 8;
        long d = 0;
        for (int i = 0; i < bitsPerSample; i++) {
            d |= 1 << i;
        }
        double div;
        //noinspection IfStatementWithIdenticalBranches
        if (!signed) { //Java has no unsigned values
            div = Math.floorDiv(d, 2);
        } else {
            div = Math.floorDiv(d, 2);
        }
        return (data, sampleDataSetter) -> {
            if (data.length % bytes != 0 || (data.length / bytes) % 2 != 0) {
                throw new IllegalArgumentException();
            }
            double[] buf = sampleDataSetter.getData().getBuffer();
            int loop = data.length / bytes;
            for (int l = 0; l < loop; l++) {
                long value = 0;
                for (int i = 0; i < bytes; i++) {
                    int offset = bigEndian ? bytes - i - 1 : i;
                    value |= (data[l * bytes + i] & 0xff) << (offset * 8);
                }
                if (value > (Math.pow(2, bitsPerSample - 1) - 1)) {
                    value = (int) (-1 * Math.pow(2, bitsPerSample) + value);
                }
                buf[l] = value / div;
            }
        };
    }

}
