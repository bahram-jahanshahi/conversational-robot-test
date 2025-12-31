package se.bahram.robotic.coversational_robot_test.usecases.voice_activity_detector.applications.services;

import javax.sound.sampled.*;
import java.util.Arrays;

public final class Mic16kMono implements AutoCloseable {
    private final TargetDataLine line;

    public Mic16kMono(int sampleRate) throws LineUnavailableException {
        AudioFormat fmt = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                sampleRate,
                16,
                1,
                2,
                sampleRate,
                false
        );

        DataLine.Info info = new DataLine.Info(TargetDataLine.class, fmt);
        this.line = (TargetDataLine) AudioSystem.getLine(info);
        this.line.open(fmt);
        this.line.start();
    }

    /** Reads exactly nSamples of 16-bit PCM and returns raw little-endian bytes. */
    public byte[] readPcmBytes(int nSamples) {
        byte[] buf = new byte[nSamples * 2];
        int off = 0;
        while (off < buf.length) {
            int r = line.read(buf, off, buf.length - off);
            if (r <= 0) break;
            off += r;
        }
        return buf;
    }

    /** Convert PCM16LE bytes to float[-1..1] for VAD. */
    public static float[] pcm16leToFloat(byte[] pcm) {
        int nSamples = pcm.length / 2;
        float[] out = new float[nSamples];
        for (int i = 0; i < nSamples; i++) {
            int lo = pcm[i * 2] & 0xFF;
            int hi = pcm[i * 2 + 1]; // signed
            short s = (short) ((hi << 8) | lo);
            out[i] = s / 32768.0f;
        }
        return out;
    }

    @Override public void close() {
        line.stop();
        line.close();
    }
}
