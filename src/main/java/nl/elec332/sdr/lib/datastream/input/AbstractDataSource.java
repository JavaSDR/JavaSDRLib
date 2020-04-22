package nl.elec332.sdr.lib.datastream.input;

import com.google.common.collect.Lists;
import nl.elec332.sdr.lib.api.datastream.IDataSource;

import java.util.List;

/**
 * Created by Elec332 on 27-3-2020
 */
public abstract class AbstractDataSource implements IDataSource {

    public AbstractDataSource() {
        this.listeners = Lists.newArrayList();
    }

    private final List<Runnable> listeners;
    private boolean stopped;

    public final boolean isStopped() {
        return stopped;
    }

    @Override
    public void stop() {
        listeners.forEach(Runnable::run);
        this.stopped = true;
    }

    @Override
    public final void addStopListener(Runnable listener) {
        this.listeners.add(listener);
    }

}
