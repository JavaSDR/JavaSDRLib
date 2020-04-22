package nl.elec332.sdr.lib.datastream.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import nl.elec332.sdr.lib.api.datastream.IDataProcessingStep;
import nl.elec332.sdr.lib.api.datastream.ISampleData;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

/**
 * Created by Elec332 on 7-2-2020
 */
public class PipelineProcessor {

    public static Runnable start(ThreadGroup threadGroup, Runnable stopListener, Consumer<DefaultSampleData> start, ToIntFunction<Integer> inputBufSize, Consumer<ISampleData> stop, List<IDataProcessingStep> steps, int samples, int sampleRate) {
        boolean[] stopp = {false};
        List<Runnable> threads = Lists.newArrayList();
        IntFunction<DefaultSampleData> creat = samplez -> new DefaultSampleData(samplez, sampleRate);
        Collection<Thread> allThreads = Sets.newHashSet();
        Runnable stopper = () -> {
            if (!stopp[0]) {
                new Thread(threadGroup, () -> {
                    try {
                        Thread.sleep(1050);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    allThreads.forEach(t -> {
                        if (t.isAlive()) {
                            try {
                                t.interrupt();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    allThreads.forEach(t -> {
                        if (t.isAlive()) {
                            System.err.println("Force killing thread: " + t.getName());
                            System.err.println("This should never happen, please report this issue on GitHub!");
                            try {
                                //noinspection deprecation
                                t.stop();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    allThreads.clear();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.gc();
                }, "Cleanup thread").start();
            }
            stopp[0] = true;
            stopListener.run();
        };
        IPipelinePart prev = new PipelinePartStart(start, inputBufSize, creat, samples, () -> stopp[0], stopper);
        threads.add(prev);
        for (IDataProcessingStep step : steps) {
            prev = new PipelinePartMiddle(prev, step, creat, samples, () -> stopp[0], stopper);
            threads.add(prev);
        }
        if (stop != null) {
            threads.add(new PipelinePartEnd(prev, stop, () -> stopp[0], stopper));
        }
        threads.forEach(r -> {
            Thread t = new Thread(threadGroup, r, "PipeLinePart @ " + threadGroup.getName() + " - ID: " + r.hashCode());
            allThreads.add(t);
            t.start();
        });
        return stopper;
    }

}
