package nl.elec332.sdr.lib.datastream.impl;

import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 7-2-2020
 */
interface IPipelinePart extends Runnable, Consumer<DefaultSampleData>, Supplier<BlockingQueue<DefaultSampleData>> {

}