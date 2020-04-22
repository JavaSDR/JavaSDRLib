package nl.elec332.sdr.lib.datastream.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import nl.elec332.sdr.lib.api.datastream.IDataProcessingStep;
import nl.elec332.sdr.lib.api.datastream.IDataSource;
import nl.elec332.sdr.lib.api.datastream.IPipeline;
import nl.elec332.sdr.lib.api.datastream.ISampleData;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

/**
 * Created by Elec332 on 7-2-2020
 */
public class PipelineImpl implements IPipeline {

    public PipelineImpl(final IDataSource source) {
        this.input = data -> source.readData(data.reader);
        this.inputBufSize = source::getOutputBufferSize;
        this.steps = Lists.newArrayList();
        this.sampleRate = source.getSampleRate();
        this.sampleSize = source.getSampleSize();
        this.children = Sets.newHashSet();
        this.source = source;
        this.source.addStopListener(this::onSourceStopped);

        this.startStopLock = new ReentrantLock();
        this.threadGroup = new ThreadGroup("Pipeline Group " + hashCode());
    }

    private final int sampleRate;
    private final Set<IPipeline> children;
    private final List<IDataProcessingStep> steps;
    private final IDataSource source;
    private int sampleSize;

    private Consumer<DefaultSampleData> input;
    private ToIntFunction<Integer> inputBufSize;
    private Consumer<ISampleData> lastStep;
    private boolean end = false;

    private final ReentrantLock startStopLock;
    private final ThreadGroup threadGroup;
    private Runnable started = null;
    private boolean sourceStopped = false;

    private void onSourceStopped() {
        if (isRunning()) {
            this.sourceStopped = true;
            started.run();
        }
    }

    @Override
    public IPipeline div2() {
        this.sampleSize /= 2;
        return this;
    }

    @Override
    public IPipeline modify(Function<IPipeline, Consumer<ISampleData>> modifier_) {
        Consumer<ISampleData> modifier = checkEnd(modifier_);
        addMerged(oldInput -> data -> {
            oldInput.accept(data);
            modifier.accept(data);
        }, prev -> new IDataProcessingStep() {

            @Override
            public void process(ISampleData in, ISampleData out) {
                prev.process(in, out);
                modifier.accept(out);
            }

            @Override
            public int getOutputBufferSize(int inputSamples) {
                return prev.getOutputBufferSize(inputSamples);
            }

        });
        return this;
    }

    @Override
    public IPipeline modifyParallel(Function<IPipeline, Consumer<ISampleData>> modifier_) {
        Consumer<ISampleData> modifier = checkEnd(modifier_);
        this.steps.add(new RecycleDataProcessingStep(modifier));
        return this;
    }

    @Override
    public IPipeline andThen(Function<IPipeline, IDataProcessingStep> step_) {
        IDataProcessingStep step = checkEnd(step_);
        addMerged(oldInput -> {
            final DefaultSampleData link = new DefaultSampleData(inputBufSize.applyAsInt(sampleSize), sampleRate);
            this.inputBufSize = step::getOutputBufferSize;
            return data -> {
                oldInput.accept(link);
                step.process(link, data);
            };

        }, prev -> {
            final DefaultSampleData link = new DefaultSampleData(prev.getOutputBufferSize(sampleSize), sampleRate);
            return new IDataProcessingStep() {

                @Override
                public void process(ISampleData in, ISampleData out) {
                    prev.process(in, link);
                    step.process(link, out);
                }

                @Override
                public int getOutputBufferSize(int inputSamples) {
                    return step.getOutputBufferSize(inputSamples);
                }

            };
        });
        return this;
    }

    @Override
    public IPipeline andThenParallel(Function<IPipeline, IDataProcessingStep> step_) {
        IDataProcessingStep step = checkEnd(step_);
        this.steps.add(step);
        return this;
    }

    @Override
    public IPipeline end(Function<IPipeline, Consumer<ISampleData>> lastStep_) {
        Consumer<ISampleData> lastStep = checkEnd(lastStep_);

        addMerged(oldInput -> {
            final DefaultSampleData link = new DefaultSampleData(inputBufSize.applyAsInt(sampleSize), sampleRate);
            this.inputBufSize = i -> -1;
            return noop -> {
                oldInput.accept(link);
                lastStep.accept(link);
            };
        }, prev -> {
            final DefaultSampleData link = new DefaultSampleData(prev.getOutputBufferSize(sampleSize), sampleRate);
            this.lastStep = sampleData -> {
                prev.process(sampleData, link);
                lastStep.accept(link);
            };
            return null;
        });
        this.end = true;
        return this;
    }

    @Override
    public IPipeline endParallel(Function<IPipeline, Consumer<ISampleData>> lastStep_) {
        this.lastStep = checkEnd(lastStep_);
        this.end = true;
        return this;
    }

    @Override
    public boolean addChild(IPipeline child) {
        return children.add(child);
    }

    @Override
    public int getSampleRate() {
        return sampleRate;
    }

    @Override
    public int getSampleSize() {
        return sampleSize;
    }

    @Override
    public void start() {
        if (!end) {
            throw new IllegalStateException();
        }
        startStopLock.lock();
        if (isRunning()) {
            throw new IllegalStateException();
        }
        try {
            started = PipelineProcessor.start(threadGroup, this::onStopped, input, inputBufSize, lastStep, steps, sampleSize, sampleRate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        startStopLock.unlock();
    }

    @Override
    public boolean isRunning() {
        return started != null;
    }

    @Override
    public void stop() {
        if (!isRunning()) {
            throw new IllegalStateException();
        }
        started.run();
    }

    private void addMerged(UnaryOperator<Consumer<DefaultSampleData>> startMerger, UnaryOperator<IDataProcessingStep> middleMerger) {
        if (steps.isEmpty()) {
            final Consumer<DefaultSampleData> oldInput = input;
            this.input = startMerger.apply(oldInput);
        } else {
            final IDataProcessingStep prev = steps.get(steps.size() - 1);
            steps.remove(prev);
            IDataProcessingStep neww = middleMerger.apply(prev);
            if (neww != null) {
                steps.add(neww);
            }
        }
    }

    private void onStopped() {
        if (started == null) {
            return;
        }
        startStopLock.lock();
        children.forEach(p -> {
            if (p.isRunning()) {
                p.stop();
            }
        });
        Runnable ref = started;
        started = null;
        if (ref != null) {
            ref.run();
        }
        if (this.sourceStopped) {
            this.sourceStopped = false;
        } else {
            source.stop();
        }
        startStopLock.unlock();
        System.out.println("STOP: " + toString());
    }

    @Nonnull
    private <T> T checkEnd(Function<IPipeline, T> func) {
        if (end) {
            throw new IllegalStateException();
        }
        Objects.requireNonNull(func);
        T ret = func.apply(this);
        return Objects.requireNonNull(ret);
    }

}
