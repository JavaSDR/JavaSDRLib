package nl.elec332.sdr.lib.datastream.input;

import nl.elec332.sdr.lib.api.datastream.IDataSource;
import nl.elec332.sdr.lib.api.datastream.ISampleDataSetter;
import nl.elec332.sdr.lib.api.source.IInputEventListener;
import nl.elec332.sdr.lib.api.source.IInputSource;
import nl.elec332.sdr.lib.api.util.EndOfDataStreamException;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Elec332 on 18-3-2020
 */
public class InputSourceConverter<T> implements IDataSource {

    public InputSourceConverter(IInputSource<T> source) {
        this.source = source;
        int len = source.getSampleRate() / source.getSamplesPerBuffer();
        this.in = new ArrayBlockingQueue<>(len);
        this.out = new ArrayBlockingQueue<>(len);
        for (int i = 0; i < len; i++) {
            in.offer(source.createBuffer());
        }
        start();
    }

    private final IInputSource<T> source;

    private final ReentrantLock lock = new ReentrantLock(true);
    private final AtomicBoolean START = new AtomicBoolean(false);
    private final AtomicBoolean eods = new AtomicBoolean(false);

    private ArrayBlockingQueue<T> in, out;

    private void start() {
        this.source.startSampling(source -> {
            try {
                if (!START.get()) {
                    return;
                }
                if (eods.get()) {
                    Thread.sleep(10);
                    return;
                }
                lock.lock();
                T working;
                try {
                    working = in.poll(100, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                if (working == null) {
                    System.out.println("Dropped sample, exiting...");
                    throw new BufferOverflowException(); //Insurance policy
                }
                try {
                    source.accept(working);
                } catch (EndOfDataStreamException e) {
                    eods.set(true);
                    return;
                }
                out.offer(working);
                lock.unlock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void readData(ISampleDataSetter dataSetter) throws EndOfDataStreamException {
        if (!START.get()) {
            START.set(true);
            source.onRecordingStarted();
        }
        T dat;
        try {
            dat = out.poll(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        if (dat == null) {
            if (eods.get()) {
                eods.set(false);
                throw new EndOfDataStreamException();
            }
            throw new BufferUnderflowException();
        }
        dataSetter.setCenterFrequency(getFrequency());
        source.setSampleData(dataSetter, dat);
        in.offer(dat);
    }

    @Override
    public long getFrequency() {
        return this.source.getFrequency();
    }

    @Override
    public int getSampleRate() {
        return this.source.getSampleRate();
    }

    @Override
    public int getSampleSize() {
        return this.source.getSamplesPerBuffer();
    }

    @Override
    public int getOutputBufferSize(int inputSamples) {
        return inputSamples * 2; // IQ
    }

    @Override
    public void stop() {
        this.source.stop();
    }

    @Override
    public void addStopListener(Runnable listener) {
        source.addListener(new IInputEventListener<IInputSource<T>>() {

            @Override
            public void onRxStopped(IInputSource<T> device) {
                listener.run();
            }

        });
    }

}
