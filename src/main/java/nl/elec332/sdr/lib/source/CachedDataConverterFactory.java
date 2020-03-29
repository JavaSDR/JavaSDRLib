package nl.elec332.sdr.lib.source;

import nl.elec332.sdr.lib.api.datastream.ISampleDataSetter;
import nl.elec332.sdr.lib.api.util.IDataConverter;
import nl.elec332.sdr.lib.api.util.IDataConverterFactory;

import javax.sound.sampled.AudioFormat;

/**
 * Created by Elec332 on 24-3-2020
 */
public enum CachedDataConverterFactory implements IDataConverterFactory {

    INSTANCE;

    private IDataConverter eight, sixteenLittle, sixteenBig;

    public IDataConverter createConverter(int bitsPerSample, boolean bigEndian, AudioFormat.Encoding encoding) {
        if (bitsPerSample > 16) {
            return DynamicDataConverterFactory.INSTANCE.createConverter(bitsPerSample, bigEndian, encoding);
        }
        if (encoding != AudioFormat.Encoding.PCM_SIGNED && encoding != AudioFormat.Encoding.PCM_UNSIGNED) {
            throw new IllegalArgumentException();
        }
        return createConverter(bitsPerSample, bigEndian, encoding == AudioFormat.Encoding.PCM_SIGNED);
    }

    public IDataConverter createConverter(int bitsPerSample, boolean bigEndian, boolean signed) {
        if (bitsPerSample > 16) {
            return DynamicDataConverterFactory.INSTANCE.createConverter(bitsPerSample, bigEndian, signed);
        }
        if (bitsPerSample == 8) {
            if (eight == null) {
                eight = new EightBitIQConverter();
            }
            return eight;
        } else if (bitsPerSample == 16) {
            if (bigEndian) {
                if (sixteenBig == null) {
                    sixteenBig = new SixteenBitIQConverter(true);
                }
                return sixteenBig;
            } else {
                if (sixteenLittle == null) {
                    sixteenLittle = new SixteenBitIQConverter(false);
                }
                return sixteenLittle;
            }
        }
        throw new IllegalArgumentException();
    }

    static class EightBitIQConverter extends AbstractCachedIQConverter {

        public EightBitIQConverter() {
            super(Byte.MIN_VALUE);
            lookupTable = new double[total];
            for (int i = 0; i < lookupTable.length; i++) {
                lookupTable[i] = getValue(i);
            }
        }

        protected final double[] lookupTable;

        @Override
        public void readData(byte[] raw, ISampleDataSetter sampleData) {
            double[] data = sampleData.getData().getBuffer();
            for (int i = 0; i < raw.length; i++) {
                data[i] = lookupTable[raw[i] + offset];
            }
        }

    }

    static class SixteenBitIQConverter extends AbstractCachedIQConverter {

        public SixteenBitIQConverter(boolean bigEndian) {
            super(Short.MIN_VALUE);
            this.lookup = new double[256][];
            for (int i = 0; i < lookup.length; i++) {
                lookup[i] = new double[lookup.length];
            }
            for (int i = 0; i < total; i++) {
                int b1, b2;
                if (bigEndian) {
                    b2 = i & 0xff;
                    b1 = (i >> 8) & 0xff;
                } else {
                    b1 = i & 0xff;
                    b2 = (i >> 8) & 0xff;
                }
                int value = i;
                if (value > max) {
                    value = lower + value;
                }
                this.lookup[b1][b2] = getValue(value + offset);
            }
        }


        private final double[][] lookup;

        @Override
        public void readData(byte[] raw, ISampleDataSetter sampleData) {
            double[] data = sampleData.getData().getBuffer();
            for (int i = 0; i < raw.length / 2; i++) {
                data[i] = lookup[raw[i * 2] & 0xff][raw[i * 2 + 1] & 0xff];
            }
        }

    }

    static abstract class AbstractCachedIQConverter implements IDataConverter {

        public AbstractCachedIQConverter(int minVal) {
            offset = -minVal;
            max = offset - 1;
            lower = minVal * 2;
            total = -lower;
        }

        protected final int offset, max, lower, total;

        protected double getValue(int i) {
            //return (i - offset) / (offset - 1d);
            return (i - offset - 0.5) / (offset - 0.5d);
        }

    }

}
