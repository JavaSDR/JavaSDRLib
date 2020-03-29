package nl.elec332.sdr.lib.extensions.fft;

import nl.elec332.sdr.lib.api.extensions.IFFTLibrary;
import nl.elec332.sdr.lib.api.fft.IFFTPlan;
import nl.elec332.sdr.lib.api.util.IDoubleArray;
import org.jtransforms.fft.DoubleFFT_1D;

/**
 * Created by Elec332 on 7-2-2020
 */
class JavaFFTLibrary implements IFFTLibrary {

    @Override
    public IFFTPlan createPlan(int sampleSize, IDoubleArray out, boolean forward) {
        return createPlan(sampleSize, null, out, forward);
    }

    @Override
    public IFFTPlan createPlan(int sampleSize, IDoubleArray in, IDoubleArray out, boolean forward) {
        return new JTransformsPlan(sampleSize, false, in, out);
    }

    static class JTransformsPlan implements IFFTPlan {

        private JTransformsPlan(int sampleSize, boolean forward, IDoubleArray in, IDoubleArray out) {
            this.plan = new DoubleFFT_1D(sampleSize);
            this.forward = forward;
            this.in = in;
            this.out = out;
        }

        private final DoubleFFT_1D plan;
        private final boolean forward;
        private final IDoubleArray in, out;

        @Override
        public void execute(IDoubleArray in) {
            execute_(in);
        }

        @Override
        public void execute() {
            if (in == null) {
                execute_(out);
                return;
            }
            execute_(in);
        }

        private void execute_(IDoubleArray in) {
            if (forward) {
                plan.complexForward(in.getBuffer());
            } else {
                plan.complexInverse(in.getBuffer(), false);
            }
            out.setData(in.getBuffer());
        }

    }

}
