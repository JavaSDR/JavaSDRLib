package nl.elec332.sdr.lib.datastream.impl;

import com.google.common.collect.Queues;
import nl.elec332.sdr.lib.api.util.EndOfDataStreamException;

import java.nio.BufferOverflowException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

/**
 * Created by Elec332 on 7-2-2020
 */
class PipelinePartStart extends AbstractPipelinePart implements IPipelinePart {

    PipelinePartStart(Consumer<DefaultSampleData> start, ToIntFunction<Integer> inputBufSize, IntFunction<DefaultSampleData> dataInit, int samplesIn, BooleanSupplier isStopped, Runnable stopper) {
        super(isStopped, stopper);
        this.start = start;
        int siz = inputBufSize.applyAsInt(samplesIn);
        if (siz < 0) {
            out = preOut = null;
        } else {
            this.out = Queues.newArrayBlockingQueue(4);
            this.preOut = Queues.newArrayBlockingQueue(4);
            for (int i = 0; i < 4; i++) {
                this.preOut.offer(dataInit.apply(siz));
            }
        }
    }

    private final Consumer<DefaultSampleData> start;
    private final BlockingQueue<DefaultSampleData> out, preOut;

    @Override
    public void run() {
        DefaultSampleData dat;
        while (!isStopped.getAsBoolean()) {
            if (out == null) {
                try {
                    start.accept(null);
                } catch (EndOfDataStreamException e) {
                    if (!isStopped.getAsBoolean()) {
                        System.out.println("END OF STREAM");
                        stopper.run();
                    }
                } catch (BufferOverflowException e) {
                    if (!isStopped.getAsBoolean()) {
                        e.printStackTrace();
                        System.out.println("BUFFER OVERFLOW");
                        stopper.run();
                    }
                }
            } else {
                try {
                    dat = preOut.poll(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    System.out.println("Output polling interrupted!");
                    stopper.run();
                    break;
                }
                if (dat == null) {
                    continue;
                }
                try {
                    start.accept(dat);
                } catch (EndOfDataStreamException e) {
                    if (!isStopped.getAsBoolean()) {
                        System.out.println("END OF STREAM");
                        stopper.run();
                    }
                } catch (BufferOverflowException e) {
                    if (!isStopped.getAsBoolean()) {
                        e.printStackTrace();
                        System.out.println("BUFFER OVERFLOW");
                        stopper.run();
                    }
                }
                out.offer(dat);
            }
        }
    }

    @Override
    public void accept(DefaultSampleData sampleData) {
        preOut.offer(sampleData);
    }

    @Override
    public BlockingQueue<DefaultSampleData> get() {
        return out;
    }

}
