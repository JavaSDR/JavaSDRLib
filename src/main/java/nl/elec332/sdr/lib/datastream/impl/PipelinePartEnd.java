package nl.elec332.sdr.lib.datastream.impl;

import nl.elec332.sdr.lib.api.datastream.ISampleData;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 7-2-2020
 */
class PipelinePartEnd extends AbstractPipelinePart {

    PipelinePartEnd(IPipelinePart in, Consumer<ISampleData> end, BooleanSupplier isStopped, Runnable stopper) {
        super(isStopped, stopper);
        this.in = in.get();
        this.recycle = in;
        this.end = end;
    }

    private final Consumer<DefaultSampleData> recycle;
    private final BlockingQueue<DefaultSampleData> in;
    private final Consumer<ISampleData> end;

    @Override
    public void run() {
        DefaultSampleData dat;
        while (!isStopped.getAsBoolean()) {
            try {
                dat = in.poll(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println("Input polling interrupted!");
                stopper.run();
                break;
            }
            if (dat == null) {
                continue;
            }
            end.accept(dat);
            recycle.accept(dat);
        }
    }

}
