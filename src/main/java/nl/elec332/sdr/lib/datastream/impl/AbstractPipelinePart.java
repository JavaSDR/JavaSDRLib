package nl.elec332.sdr.lib.datastream.impl;

import java.util.function.BooleanSupplier;

/**
 * Created by Elec332 on 7-2-2020
 */
abstract class AbstractPipelinePart implements Runnable {

    public AbstractPipelinePart(BooleanSupplier isStopped, Runnable stopper) {
        this.isStopped = isStopped;
        this.stopper = stopper;
    }

    protected final BooleanSupplier isStopped;
    protected final Runnable stopper;

}
