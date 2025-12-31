package se.bahram.robotic.coversational_robot_test.usecases.voice_activity_detector.applications.services;

import javax.sound.sampled.*;

public final class Mic48kMono implements AutoCloseable{

    public static final int CAPTURE_SR = 48_000;

    private final TargetDataLine line;

    public Mic48kMono() throws LineUnavailableException {
        AudioFormat fmt = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                CAPTURE_SR,
                16,
                1,
                2,
                CAPTURE_SR,
                false
        );
        System.out.println("Trying to open mic with format: " + fmt.toString());
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, fmt);
        System.out.println("Supported? " + AudioSystem.isLineSupported(info));
        line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(fmt);
        line.start();

        System.out.println("Mic48kMono started with format:");
        System.out.println(info.toString());
    }

    /** Reads exactly nSamples at 48kHz (16-bit mono) and returns PCM16LE bytes. */
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

    @Override public void close() {
        line.stop();
        line.close();
    }
}
