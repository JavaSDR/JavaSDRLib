package nl.elec332.sdr.lib.source.waviq;

import nl.elec332.sdr.lib.SDRLibrary;
import nl.elec332.sdr.lib.api.datastream.ISampleDataSetter;
import nl.elec332.sdr.lib.api.util.EndOfDataStreamException;
import nl.elec332.sdr.lib.api.util.IDataConverter;
import nl.elec332.sdr.lib.api.util.ISoundInput;
import nl.elec332.sdr.lib.source.device.AbstractDevice;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 21-3-2020
 */
public class IQAudioSource extends AbstractDevice<byte[]> {

    public IQAudioSource(ISoundInput soundInput, int samplesPerBuffer) {
        AudioFormat audioFormat = soundInput.getAudioFormat();
        this.waitSamples = true;
        this.soundInput = soundInput;
        this.samplesperBuffer = samplesPerBuffer;
        this.frameSize = audioFormat.getFrameSize();
        this.sampleRate = Math.round(audioFormat.getSampleRate());
        this.timePerReadNano = (int) Math.floor(10E8 / (this.sampleRate / (double) this.samplesperBuffer));
        this.dataConverter = SDRLibrary.getInstance().getCachedDataConverterFactory().createConverter(audioFormat.getSampleSizeInBits(), audioFormat.isBigEndian(), audioFormat.getEncoding());
        checkAudioFormat(audioFormat);
    }

    private final ISoundInput soundInput;
    private final IDataConverter dataConverter;
    private final int samplesperBuffer, sampleRate, frameSize, timePerReadNano;
    private boolean waitSamples;
    private long left;

    private static void checkAudioFormat(AudioFormat audioFormat) {
        if (audioFormat.getChannels() != 2) { //I and Q data
            throw new UnsupportedOperationException();
        }
        System.out.println("Opening IQ file: " + audioFormat);
    }

    public void setWaitBetweenSamples(boolean b) {
        this.waitSamples = b;
    }

    @Override
    protected void startDeviceSampling(Consumer<Consumer<byte[]>> consumer) {
        new Thread(() -> {
            while (!isRecording()) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    // NBC
                }
            }
            Consumer<byte[]> processor = dataBuffer -> {
                try {
                    if (soundInput.hasReachedEnd()) {
                        throw new EndOfDataStreamException();
                    }
                    soundInput.read(dataBuffer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
            while (isActive()) {
                long nano = System.nanoTime();
                consumer.accept(processor);
                if (waitSamples) {
                    long time = System.nanoTime() - nano;
                    if (timePerReadNano > time) {
                        time = timePerReadNano - time;
                    } else {
                        continue;
                    }
                    try {
                        time += this.left;
                        this.left = time % (long) 10E5;
                        time = Math.floorDiv(time, (long) 10E5);
                        if (time > 1) {
                            Thread.sleep(time - 1);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    @Override
    public void setSampleData(ISampleDataSetter dataSetter, byte[] dataBuffer) {
        dataConverter.readData(dataBuffer, dataSetter);
    }

    @Override
    protected void stopDevice() {
    }

    public boolean hasReachedEnd() {
        try {
            return this.soundInput.hasReachedEnd();
        } catch (IOException e) {
            return true;
        }
    }

    @Override
    protected void closeDevice() {
        this.soundInput.close();
    }

    @Override
    public int getSampleRate() {
        return this.sampleRate;
    }

    @Override
    public int getSamplesPerBuffer() {
        return this.samplesperBuffer;
    }

    @Override
    public byte[] createBuffer() {
        return new byte[samplesperBuffer * frameSize];
    }

}
