package nl.elec332.sdr.lib.extensions;


import nl.elec332.sdr.lib.util.buffer.AbstractFiFoDoubleRingBuffer;
import org.bytedeco.javacpp.DoublePointer;

/**
 * Created by Elec332 on 26-3-2020
 */
class NativeFiFoDoubleRingBuffer extends AbstractFiFoDoubleRingBuffer<DoublePointer> {

    NativeFiFoDoubleRingBuffer(int maxSize) {
        super(maxSize);
    }

    @Override
    protected DoublePointer createBuffer(int maxLength) {
        return new DoublePointer(maxLength);
    }

    @Override
    protected void get(int position, double[] data, int arrayIndex, int length) {
        DoublePointer p = buffer.position(position);
        p.get(data, arrayIndex, length);
    }

    @Override
    protected void put(int position, double[] data, int arrayIndex, int length) {
        DoublePointer p = buffer.position(position);
        p.put(data, arrayIndex, length);
    }

}