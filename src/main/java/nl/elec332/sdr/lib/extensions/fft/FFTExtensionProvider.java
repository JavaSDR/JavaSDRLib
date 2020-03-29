package nl.elec332.sdr.lib.extensions.fft;

import nl.elec332.sdr.lib.api.ISDRExtensionProvider;
import nl.elec332.sdr.lib.api.ISDRLibrary;
import nl.elec332.sdr.lib.api.ImplementationType;
import nl.elec332.sdr.lib.api.extensions.IFFTLibrary;
import org.bytedeco.javacpp.Loader;

/**
 * Created by Elec332 on 29-3-2020
 */
public class FFTExtensionProvider implements ISDRExtensionProvider {

    @Override
    public void registerExtensions(ISDRLibrary library) {
        library.addLibraryExtension(IFFTLibrary.class, new JavaFFTLibrary());
    }

    @Override
    public void registerExtensionImplementations(ISDRLibrary library) {
        try {
            Loader.load(org.bytedeco.fftw.global.fftw3.class);
            library.registerLibraryImplementation(IFFTLibrary.class, new NativeFFTLibrary(), ImplementationType.NATIVE_FAST);
        } catch (Exception e) {
            System.out.println("Failed to load native FFT implementation, this will have a significant performance impact.");
        }
    }

    @Override
    public void afterRegister(ISDRLibrary library) {
    }

}
