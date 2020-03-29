package nl.elec332.sdr.lib.datastream.impl;

import nl.elec332.sdr.lib.api.datastream.ISampleData;
import nl.elec332.sdr.lib.api.datastream.ISampleDataSetter;
import nl.elec332.sdr.lib.api.util.IDoubleArray;
import nl.elec332.sdr.lib.extensions.SDRLibraryExtensions;

/**
 * Created by Elec332 on 7-2-2020
 */
class DefaultSampleData implements ISampleData {

    DefaultSampleData(int samples, int samplerate) {
        this.samples = samples;
        this.sampleRate = samplerate;
        this.data = SDRLibraryExtensions.createNewArray(new double[this.samples]); //Pre-allocate buffer
        this.reader = new ISampleDataSetter() {

            @Override
            public void setCenterFrequency(long freq) {
                DefaultSampleData.this.centerFreq = freq;
            }

            @Override
            public void setSampleSize(int samples) {
                DefaultSampleData.this.setSampleSize(samples);
            }

            @Override
            public IDoubleArray getData() {
                return DefaultSampleData.this.data;
            }

        };
    }


    private final IDoubleArray data;
    private int samples, sampleRate;
    final ISampleDataSetter reader;

    private long centerFreq = 0;

    @Override
    public long getCenterFrequency() {
        return this.centerFreq;
    }

    @Override
    public int getSampleRate() {
        return this.sampleRate;
    }

    @Override
    public int getSampleSize() {
        return this.samples;
    }

    @Override
    public void setSampleSize(int samples) {
        this.samples = samples;
    }

    @Override
    public IDoubleArray getData() {
        return this.data;
    }

}
