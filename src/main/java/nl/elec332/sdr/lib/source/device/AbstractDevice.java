package nl.elec332.sdr.lib.source.device;

import com.google.common.collect.Lists;
import nl.elec332.sdr.lib.api.source.IInputEventListener;
import nl.elec332.sdr.lib.api.source.IInputSource;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 27-3-2020
 */
public abstract class AbstractDevice<T> implements IInputSource<T> {

    private List<IInputEventListener<IInputSource<T>>> lcb;
    private AtomicBoolean active, recStarted;

    protected void iterateListeners(Consumer<? super IInputEventListener<IInputSource<T>>> action) {
        if (lcb != null) {
            lcb.forEach(action);
        }
    }

    @Override
    public void addListener(IInputEventListener<IInputSource<T>> listener) {
        if (lcb == null) {
            lcb = Lists.newArrayList();
        }
        lcb.add(listener);
    }

    @Override
    public final void startSampling(Consumer<Consumer<T>> bufferGetter) {
        checkBools();
        active.set(true);
        startDeviceSampling(bufferGetter);
        iterateListeners(l -> l.onRxStarted(this));
    }

    protected abstract void startDeviceSampling(Consumer<Consumer<T>> bufferGetter);

    @Override
    public void onRecordingStarted() {
        checkBools();
        this.recStarted.set(true);
    }

    @Override
    public final void stop() {
        checkBools();
        this.active.set(false);
        this.recStarted.set(false);
        stopDevice();
        iterateListeners(l -> l.onRxStopped(this));
    }

    @Override
    public final void close() {
        checkBools();
        if (active.get()) {
            stop();
        }
        closeDevice();
        iterateListeners(l -> l.onClosed(this));
    }

    protected abstract void stopDevice();

    protected abstract void closeDevice();

    protected boolean isRecording() {
        return recStarted.get();
    }

    protected boolean isActive() {
        return active.get();
    }

    private void checkBools() {
        if (active == null) {
            active = new AtomicBoolean(false);
            recStarted = new AtomicBoolean(false);
        }
    }

}
