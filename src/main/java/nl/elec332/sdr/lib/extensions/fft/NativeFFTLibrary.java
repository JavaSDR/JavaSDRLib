package nl.elec332.sdr.lib.extensions.fft;

import nl.elec332.sdr.lib.api.extensions.IFFTLibrary;
import nl.elec332.sdr.lib.api.fft.IFFTPlan;
import nl.elec332.sdr.lib.api.util.IDoubleArray;
import nl.elec332.sdr.lib.extensions.SDRLibraryExtensions;

import static org.bytedeco.fftw.global.fftw3.*;

/**
 * Created by Elec332 on 7-2-2020
 */
class NativeFFTLibrary implements IFFTLibrary {

    @Override
    public IFFTPlan createPlan(int sampleSize, IDoubleArray out, boolean forward) {
        return createPlan(sampleSize, SDRLibraryExtensions.createNewArray(sampleSize * 2), out, forward);
    }

    @Override
    public IFFTPlan createPlan(int sampleSize, final IDoubleArray in, IDoubleArray out, boolean forward) {
        fftw_plan plan = fftw_plan_dft_1d(sampleSize, in.getPointer(), out.getPointer(), forward ? FFTW_FORWARD : FFTW_BACKWARD, (int) FFTW_DESTROY_INPUT);
        return new FFTWPlan(plan, in);
    }

    private static class FFTWPlan implements IFFTPlan {

        private FFTWPlan(fftw_plan plan, IDoubleArray in) {
            this.plan = plan;
            this.in = in;
        }

        private final fftw_plan plan;
        private final IDoubleArray in;

        @Override
        public void execute(IDoubleArray in) {
            this.in.copyData(in.getBuffer());
            execute();
        }

        @Override
        public void execute() {
            in.getPointer();
            fftw_execute(plan);
        }

    }

    static {
        fftw_init_threads();
        fftw_plan_with_nthreads(4);
        fftw_set_timelimit(0.003);
    }

}
