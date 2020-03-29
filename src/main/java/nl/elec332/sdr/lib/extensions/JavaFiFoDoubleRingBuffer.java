package nl.elec332.sdr.lib.extensions;

import nl.elec332.sdr.lib.util.buffer.AbstractFiFoDoubleRingBuffer;

/**
 * Created by Elec332 on 26-3-2020
 */
class JavaFiFoDoubleRingBuffer extends AbstractFiFoDoubleRingBuffer<double[]> {

    JavaFiFoDoubleRingBuffer(int maxSize) {
        super(maxSize);
    }

    @Override
    protected double[] createBuffer(int maxLength) {
        return new double[maxLength];
    }

    @Override
    protected void get(int position, double[] data, int arrayIndex, int length) {
        try {
            System.arraycopy(buffer, position, data, arrayIndex, length);
        } catch (Exception e) {
            System.out.println(buffer.length + "    " + data.length);
            System.out.println("p: " + position + "ai: " + arrayIndex + " len: " + length);
            e.printStackTrace(System.out);
        }
    }

    @Override
    protected void put(int position, double[] data, int arrayIndex, int length) {
        System.arraycopy(data, arrayIndex, buffer, position, length);
    }

}
