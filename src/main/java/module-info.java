import nl.elec332.sdr.lib.api.ISDRExtensionProvider;
import nl.elec332.sdr.lib.api.source.IInputHandler;
import nl.elec332.sdr.lib.extensions.fft.FFTExtensionProvider;
import nl.elec332.sdr.lib.extensions.filter.FilterExtensionProvider;
import nl.elec332.sdr.lib.extensions.util.UtilExtensionProvider;
import nl.elec332.sdr.lib.source.waviq.AudioInputHandler;

/**
 * Created by Elec332 on 20-4-2020
 */
module nl.elec332.sdr.lib {

    exports nl.elec332.sdr.lib;
    exports nl.elec332.sdr.lib.api;
    exports nl.elec332.sdr.lib.api.datastream;
    exports nl.elec332.sdr.lib.api.extensions;
    exports nl.elec332.sdr.lib.api.fft;
    exports nl.elec332.sdr.lib.api.filter;
    exports nl.elec332.sdr.lib.api.source;
    exports nl.elec332.sdr.lib.api.util;
    exports nl.elec332.sdr.lib.dsp;
    exports nl.elec332.sdr.lib.dsp.demodulation;
    exports nl.elec332.sdr.lib.dsp.fft;
    exports nl.elec332.sdr.lib.dsp.filter;
    exports nl.elec332.sdr.lib.extensions;
    exports nl.elec332.sdr.lib.source.device;
    exports nl.elec332.sdr.lib.source.inputhandler;
    exports nl.elec332.sdr.lib.ui;
    exports nl.elec332.sdr.lib.util;
    exports nl.elec332.sdr.lib.util.audio;
    exports nl.elec332.sdr.lib.util.buffer;

    uses IInputHandler;
    uses ISDRExtensionProvider;

    provides IInputHandler with AudioInputHandler;
    provides ISDRExtensionProvider with FFTExtensionProvider, FilterExtensionProvider, UtilExtensionProvider;

    // Dependencies
    requires jsr305;
    requires java.desktop;
    requires com.google.common;
    requires org.bytedeco.javacpp;
    requires nl.elec332.implementationmanager;
    requires transitive nl.elec332.lib.eleclib;

    //For JTransforms
    requires jdk.unsupported;

    requires static org.bytedeco.fftw;
    requires static pl.edu.icm.jlargearrays;
    requires static JTransforms;


}