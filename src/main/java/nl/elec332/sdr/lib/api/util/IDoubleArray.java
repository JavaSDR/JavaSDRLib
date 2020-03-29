package nl.elec332.sdr.lib.api.util;

import org.bytedeco.javacpp.DoublePointer;

/**
 * Created by Elec332 on 31-1-2020
 */
public interface IDoubleArray {

    int getLength();

    double[] getBuffer();

    void copyData(double[] data);

    void setData(double[] data);

    void peekBuffer(double[] peekBuf);

    void peekBuffer(double[] peekBuf, int from);

    DoublePointer getPointer();

}
