package nl.elec332.sdr.lib.extensions;

import nl.elec332.sdr.lib.api.util.IDoubleArray;
import org.bytedeco.javacpp.DoublePointer;

/**
 * Created by Elec332 on 7-2-2020
 */
class JavaDoubleArray implements IDoubleArray {

    JavaDoubleArray(int length) {
        this(new double[length]);
    }

    JavaDoubleArray(double[] data) {
        this.data = data;
        this.length = data.length;
    }

    private final int length;
    private double[] data;

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public double[] getBuffer() {
        return data;
    }

    @Override
    public void copyData(double[] data) {
        if (data.length != length) {
            throw new IllegalArgumentException();
        }
        System.arraycopy(data, 0, this.data, 0, length);
    }

    @Override
    public void setData(double[] data) {
        if (data.length != length) {
            throw new IllegalArgumentException();
        }
        this.data = data;
    }

    @Override
    public void peekBuffer(double[] peekBuf) {
        if (peekBuf.length != length) {
            throw new IllegalArgumentException();
        }
        System.arraycopy(data, 0, peekBuf, 0, length);
    }

    @Override
    public void peekBuffer(double[] peekBuf, int from) {
        if (peekBuf.length + from > length) {
            throw new IllegalArgumentException();
        }
        System.arraycopy(data, from, peekBuf, 0, peekBuf.length);
    }

    @Override
    public DoublePointer getPointer() {
        throw new UnsupportedOperationException();
    }

}
