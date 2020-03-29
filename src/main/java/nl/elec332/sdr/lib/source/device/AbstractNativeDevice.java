package nl.elec332.sdr.lib.source.device;

import com.google.common.collect.Lists;
import nl.elec332.sdr.lib.api.source.IInputEventListener;
import nl.elec332.sdr.lib.api.source.IInputSource;
import org.bytedeco.javacpp.Pointer;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 27-3-2020
 */
public abstract class AbstractNativeDevice<T> extends Pointer implements IInputSource<T> {

    private List<IInputEventListener<IInputSource<T>>> lcb;
    private AtomicBoolean started, recStarted;

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
        started.set(true);
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
        this.started.set(false);
        this.recStarted.set(false);
        stopDevice();
        iterateListeners(l -> l.onRxStopped(this));
    }

    @Override
    public final void close() {
        closeDevice();
        iterateListeners(l -> l.onClosed(this));
    }

    protected abstract void stopDevice();

    protected abstract void closeDevice();

    protected boolean hasStartedRecording() {
        return recStarted.get();
    }

    private void checkBools() {
        if (started == null) {
            started = new AtomicBoolean(false);
            recStarted = new AtomicBoolean(false);
        }
    }

}
