package nl.elec332.sdr.lib.source.inputhandler;

import nl.elec332.sdr.lib.api.source.IInputEventListener;
import nl.elec332.sdr.lib.api.source.IInputHandler;
import nl.elec332.sdr.lib.api.source.IInputSource;
import nl.elec332.sdr.lib.api.util.FailedToOpenDeviceException;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Created by Elec332 on 27-3-2020
 */
public abstract class AbstractInputHandler<D extends IInputSource<T>, T> implements IInputHandler<D>, IInputEventListener<IInputSource<T>> {

    protected AbstractInputHandler() {
        this.uiCreated = false;
        this.currentDevice = null;
        this.listeners = Collections.newSetFromMap(new WeakHashMap<>());
    }

    protected final Set<JComponent> listeners;

    private boolean uiCreated;
    private boolean started;
    private D currentDevice;

    public final boolean isStarted() {
        return started;
    }

    @Nonnull
    @Override
    public final D createDevice() throws FailedToOpenDeviceException, IllegalStateException {
        if (currentDevice != null) {
            throw new IllegalStateException();
        }
        currentDevice = createNewDevice();
        if (currentDevice == null) {
            throw new FailedToOpenDeviceException("Null device from API, invalid ID?");
        }
        modifyNewDevice(currentDevice);
        currentDevice.addListener(this);
        return currentDevice;
    }

    protected abstract D createNewDevice();

    protected void modifyNewDevice(D device) {
    }

    @Override
    public void createInterface(JPanel panel) throws UnsupportedOperationException, IllegalStateException {
        if (uiCreated) {
            throw new IllegalStateException();
        }
        if (createNewInterface(panel)) {
            uiCreated = true;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    protected boolean createNewInterface(JPanel panel) {
        return false;
    }

    @Override
    public void onRxStarted(IInputSource<T> device) {
        started = true;
        onRXChange();
    }

    @Override
    public void onRxStopped(IInputSource<T> device) {
        started = false;
        onRXChange();
    }

    private void onRXChange() {
        for (JComponent component : listeners) {
            component.setEnabled(!started);
        }
    }

    @Override
    public void onClosed(IInputSource<T> device) {
        currentDevice = null;
    }

    protected boolean isDeviceValid(D device) {
        return true;
    }

    @Nonnull
    @Override
    public Optional<D> getCurrentDevice() {
        if (this.currentDevice != null && isDeviceValid(this.currentDevice)) {
            return Optional.of(this.currentDevice);
        }
        return Optional.empty();
    }

}
