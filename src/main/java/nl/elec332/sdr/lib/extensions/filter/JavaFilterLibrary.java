package nl.elec332.sdr.lib.extensions.filter;

import nl.elec332.sdr.lib.api.extensions.IFilterLibrary;
import nl.elec332.sdr.lib.api.filter.IFilter;
import nl.elec332.sdr.lib.api.util.IDoubleArray;

/**
 * Created by Elec332 on 29-3-2020
 */
class JavaFilterLibrary implements IFilterLibrary {

    @Override
    public IFilter createFilter(double[] filter) {
        return new JavaFilterImpl(filter);
    }

    private static class JavaFilterImpl implements IFilter {

        private JavaFilterImpl(double[] filter) {
            this.filter = filter;
            this.length = filter.length;
            this.buffer = new double[this.length];
            this.bufferIndex = 0;
        }

        private final double[] filter, buffer;
        private final int length;
        private int bufferIndex;

        @Override
        public double filter(double value) {
            this.buffer[this.bufferIndex] = value;
            this.bufferIndex++;
            if (this.bufferIndex >= this.length) {
                this.bufferIndex = 0;
            }
            double ret = 0;
            for (int i = 0; i < this.length; i++) {
                ret += this.buffer[(i + this.bufferIndex) % this.length] * this.filter[i];
            }
            return ret;
        }

        @Override
        public void filter(IDoubleArray data, int length) {
            double[] in = data.getBuffer();
            for (int i = 0; i < length; i++) {
                in[i] = filter(in[i]);
            }
            for (int i = 0; i < this.length; i++) {
                this.buffer[i] = 0;
            }
        }

        @Override
        public int getFilterLength() {
            return this.length;
        }

        @Override
        public double[] getFilter() {
            double[] ret = new double[this.length];
            System.arraycopy(this.filter, 0, ret, 0, this.length);
            return ret;
        }

    }

}
