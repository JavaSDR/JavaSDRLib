package nl.elec332.sdr.lib.extensions;

import nl.elec332.sdr.lib.api.util.IDoubleArray;
import org.bytedeco.javacpp.DoublePointer;

/**
 * Created by Elec332 on 31-1-2020
 */
class NativeDoubleArray implements IDoubleArray {

    NativeDoubleArray(int length) {
        this(length, null, null);
    }

    NativeDoubleArray(double[] data) {
        this(data.length, data, null);
    }

    private NativeDoubleArray(int length, double[] arr, DoublePointer pointer) {
        this.bufferDirty = this.pointerDirty = false;
        this.length = length;
        this.buffer = arr;
        this.pointer = pointer;
    }

    private final int length;
    private boolean bufferDirty, pointerDirty;
    private double[] buffer;
    private DoublePointer pointer;

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public double[] getBuffer() {
        this.bufferDirty = true;
        checkBuffer();
        return buffer;
    }

    @Override
    public void copyData(double[] data) {
        if (data.length != length) {
            throw new IllegalArgumentException();
        }
        checkBuffer();
        System.arraycopy(data, 0, buffer, 0, length);
        this.bufferDirty = true;
    }

    @Override
    public void setData(double[] data) {
        if (data.length != length) {
            throw new IllegalArgumentException(data.length + " is not " + length);
        }
        this.buffer = data;
        this.bufferDirty = true;
    }

    @Override
    public void peekBuffer(double[] peekBuf) {
        if (peekBuf.length != length) {
            throw new IllegalArgumentException();
        }
        checkBuffer();
        System.arraycopy(buffer, 0, peekBuf, 0, length);
    }

    @Override
    public void peekBuffer(double[] peekBuf, int from) {
        if (peekBuf.length + from > length) {
            throw new IllegalArgumentException();
        }
        checkBuffer();
        System.arraycopy(buffer, from, peekBuf, 0, peekBuf.length);
    }

    @Override
    public DoublePointer getPointer() {
        this.pointerDirty = true;
        checkPointer();
        return pointer;
    }

    private void checkBuffer() {
        if (buffer == null) {
            buffer = new double[length];
            pointerDirty = pointer != null;
        }
        if (pointerDirty) {
            pointer.get(buffer);
            pointerDirty = false;
        }
    }

    private void checkPointer() {
        if (pointer == null) {
            pointer = new DoublePointer(length);
            bufferDirty = buffer != null;
        }
        if (bufferDirty) {
            pointer.put(buffer);
            bufferDirty = false;
        }
    }

}
