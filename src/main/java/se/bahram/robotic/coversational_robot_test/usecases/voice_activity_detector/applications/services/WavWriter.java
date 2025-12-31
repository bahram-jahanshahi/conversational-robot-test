package se.bahram.robotic.coversational_robot_test.usecases.voice_activity_detector.applications.services;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.*;

public final class WavWriter {
    private WavWriter() {}

    public static Path writePcm16leMonoWav(Path outFile, byte[] pcm16le, int sampleRate) throws IOException {
        AudioFormat fmt = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                sampleRate,
                16,
                1,
                2,
                sampleRate,
                false // little endian
        );

        try (ByteArrayInputStream bais = new ByteArrayInputStream(pcm16le);
             AudioInputStream ais = new AudioInputStream(bais, fmt, pcm16le.length / 2)) {

            Files.createDirectories(outFile.getParent());
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outFile.toFile());
            return outFile;
        }
    }
}
