package nl.elec332.sdr.lib.datastream.impl;

import com.google.common.collect.Queues;
import nl.elec332.sdr.lib.api.datastream.IDataProcessingStep;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/**
 * Created by Elec332 on 7-2-2020
 */
class PipelinePartMiddle extends AbstractPipelinePart implements IPipelinePart {

    PipelinePartMiddle(IPipelinePart in, IDataProcessingStep proc, IntFunction<DefaultSampleData> dataInit, int samplesIn, BooleanSupplier isStopped, Runnable stopper) {
        super(isStopped, stopper);
        this.in = in.get();
        this.inRecycler = in;
        this.proc = proc;
        this.recycle = in instanceof RecycleDataProcessingStep;
        this.out = Queues.newArrayBlockingQueue(4);
        if (recycle) {
            this.preOut = null;
        } else {
            this.preOut = Queues.newArrayBlockingQueue(4);
            for (int i = 0; i < 4; i++) {
                this.preOut.offer(dataInit.apply(proc.getOutputBufferSize(samplesIn)));
            }
        }
    }

    private final BlockingQueue<DefaultSampleData> in, out;
    private final BlockingQueue<DefaultSampleData> preOut;
    private final Consumer<DefaultSampleData> inRecycler;
    private final IDataProcessingStep proc;
    private final boolean recycle;

    private void process(DefaultSampleData in, DefaultSampleData out) {
        proc.process(in, out);
    }

    @Override
    public void accept(DefaultSampleData sampleData) {
        if (recycle) {
            inRecycler.accept(sampleData);
        } else {
            preOut.offer(sampleData);
        }
    }

    @Override
    public BlockingQueue<DefaultSampleData> get() {
        return out;
    }

    @Override
    public void run() {
        DefaultSampleData inData, outData = null;
        while (!isStopped.getAsBoolean()) {
            try {
                inData = in.poll(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println("Input polling interrupted!");
                stopper.run();
                break;
            }
            if (!recycle) {
                try {
                    outData = preOut.poll(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    System.out.println("Output polling interrupted!");
                    stopper.run();
                    break;
                }
                if (outData == null) {
                    continue;
                }
            }
            if (inData == null) {
                continue;
            }
            process(inData, outData);
            if (recycle) {
                out.offer(inData);
            } else {
                inRecycler.accept(inData);
                out.offer(outData);
            }
        }
    }

}
