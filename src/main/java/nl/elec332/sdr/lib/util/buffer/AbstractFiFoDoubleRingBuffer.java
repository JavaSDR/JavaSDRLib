package nl.elec332.sdr.lib.util.buffer;

import nl.elec332.lib.java.util.BiLock;
import nl.elec332.sdr.lib.SDRLibrary;
import nl.elec332.sdr.lib.api.datastream.IPipeline;
import nl.elec332.sdr.lib.api.datastream.ISampleData;
import nl.elec332.sdr.lib.api.datastream.ISampleDataSetter;
import nl.elec332.sdr.lib.api.util.IDoubleArray;
import nl.elec332.sdr.lib.api.util.IFiFoDoubleBuffer;
import nl.elec332.sdr.lib.datastream.PipelineHelper;
import nl.elec332.sdr.lib.datastream.input.AbstractDataSource;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

/**
 * Created by Elec332 on 27-1-2020
 */
public abstract class AbstractFiFoDoubleRingBuffer<BUF> implements IFiFoDoubleBuffer {

    public AbstractFiFoDoubleRingBuffer(int maxSize) {
        this.buffer = createBuffer(maxSize);
        this.length = maxSize;
        this.index = -1;
        this.zero = 0;
        clear();
    }

    private final BiLock lock = new BiLock();
    protected final BUF buffer;
    private final int length;

    private int index, zero;

    protected abstract BUF createBuffer(int maxLength);

    protected abstract void get(int position, double[] data, int arrayIndex, int length);

    protected abstract void put(int position, double[] data, int arrayIndex, int length);

    @Override
    public synchronized void accept(ISampleData data_) {
        IDoubleArray data = data_.getData();
        if (data.getLength() > length) {
            throw new IllegalArgumentException();
        }
        lock.waitForCondition1(() -> length - size_() > data.getLength());
        preFunc();
        if (length - size_() <= data.getLength()) { //Unlucky...
            postFunc();
            accept(data_);
            return;
        }
        add_(data);
        postFunc();
        onSizeChanged();
    }

    @Override
    public double[] read(int length) {
        preFunc();
        double[] ret = new double[length];
        read_(ret, length);
        postFunc();
        onSizeChanged();
        return ret;
    }

    @Override
    public double[] peek(int length) {
        long l = System.currentTimeMillis();
        double[] ret = new double[length];
        System.out.println("MAKETIM: " + (System.currentTimeMillis() - l));
        peek(ret);
        return ret;
    }

    @Override
    public void peek(double[] buffer) {
        preFunc();
        peek_(buffer, buffer.length);
        postFunc();
    }

    @Override
    public void clear() {
        preFunc();
        this.zero = 0;
        this.index = -1;
        postFunc();
        onSizeChanged();
    }

    @Override
    public int size() {
        int ret;
        preFunc();
        ret = size_();
        postFunc();
        return ret;
    }

    @Override
    public IPipeline createOutput(final Predicate<IFiFoDoubleBuffer> validator, final IntSupplier samples, final int maxSamplez, final int sampleRate) {
        return PipelineHelper.startPipeline(new AbstractDataSource() {

            @Override
            public void readData(ISampleDataSetter dataSetter) {
                lock.waitForCondition2(() -> {
                    if (!validator.test(AbstractFiFoDoubleRingBuffer.this)) {
                        return false;
                    }
                    int samp = samples.getAsInt();
                    if (samp > maxSamplez) {
                        throw new IllegalArgumentException();
                    }
                    return size_() >= samp;
                });
                int samplez = samples.getAsInt();
                if (samplez > maxSamplez) {
                    throw new IllegalArgumentException();
                } else if (samplez < 0) {
                    throw new IllegalArgumentException(samples.toString());
                }
                preFunc();
                if (samplez > size_()) {
                    postFunc();
                    readData(dataSetter);
                }
                read_(dataSetter.getData().getBuffer(), samplez);
                postFunc(); //End lock BEFORE processing....
                onSizeChanged();
                dataSetter.setSampleSize(samplez);
            }

            @Override
            public int getSampleRate() {
                return sampleRate;
            }

            @Override
            public int getSampleSize() {
                return maxSamplez;
            }

            @Override
            public int getOutputBufferSize(int inputSamples) {
                return maxSamplez;
            }

        });
    }

    private void add_(IDoubleArray data_) {
        double[] data = data_.getBuffer();
        if (index < 0) {
            index = 0;
        }
        int end = Math.min(data_.getLength(), length - index);
        put(index, data, 0, end);
        if (data_.getLength() > end) {
            int second = data_.getLength() - end;
            put(0, data, end, second);
            //System.arraycopy(data, end, buffer, 0, second);
            this.index = second;
        } else {
            this.index += data.length;
        }
    }

    private void read_(double[] ret, int length) {
        peek_(ret, length);
        int pre = Math.min(this.length - zero, length);
        if (pre < length) {
            zero = length - pre;
        } else {
            this.zero += pre;
        }
    }

    private void peek_(double[] buff, int length) {
        if (length > buff.length) {
            throw new IllegalArgumentException();
        }
        if (length > size_()) {
            throw new IllegalArgumentException(length + " > " + size_());
        }
        if (length < 0) {
            throw new IllegalArgumentException();
        }
        int pre = Math.min(this.length - zero, length);
        get(zero, buff, 0, pre); //Copy bottom to the return array
        if (pre < length) {
            get(0, buff, pre, length - pre);
        }
    }

    private int size_() {
        if (index < 0) {
            return 0;
        }
        if (index >= zero) { // >= instead if >, very important............. Sigh
            return index - zero;
        } else {
            return (this.length - zero) + index;
        }
    }

    private void preFunc() {
        lock_.lock();
    }

    private final ReentrantLock lock_ = new ReentrantLock(true);

    private void postFunc() {
        lock_.unlock();
    }

    private void onSizeChanged() {
        lock.notifyChange();
    }

    static {
        SDRLibrary.load();
    }

}
