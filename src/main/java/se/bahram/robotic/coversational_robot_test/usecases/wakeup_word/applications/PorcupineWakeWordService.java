package se.bahram.robotic.coversational_robot_test.usecases.wakeup_word.applications;

import ai.picovoice.porcupine.Porcupine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Service
public class PorcupineWakeWordService {

    @Value("${app.porcupine.api-key}")
    private String porcupineApiKey;

    @Value("${app.porcupine.hey-raz-keyword-file-path}")
    private String heyRazKeywordFilePath;

    public void execute() throws Exception {
        // Implementation for Porcupine Wake Word detection
        Porcupine porcupine = new Porcupine.Builder()
                .setAccessKey(porcupineApiKey)
                //.setBuiltInKeywords( new Porcupine.BuiltInKeyword[]{
                //        Porcupine.BuiltInKeyword.PORCUPINE,
                //        Porcupine.BuiltInKeyword.BUMBLEBEE,})
                .setKeywordPaths( new String[]{heyRazKeywordFilePath})
                .build();

        final int sampleRate = porcupine.getSampleRate();      // e.g., 16000
        final int frameLength = porcupine.getFrameLength();    // number of 16-bit samples per frame

        // 3) Open microphone with matching format: mono, 16-bit, little-endian PCM
        AudioFormat format = new AudioFormat(
                (float) sampleRate,
                16,
                1,
                true,
                false // little-endian
        );

        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            porcupine.delete();
            throw new LineUnavailableException("TargetDataLine not supported for format: " + format);
        }

        TargetDataLine mic = (TargetDataLine) AudioSystem.getLine(info);
        mic.open(format);

        // Shutdown hook to cleanup on Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                mic.stop();
                mic.close();
            } catch (Exception ignored) { }
            try {
                porcupine.delete();
            } catch (Exception ignored) { }
        }));

        mic.start();
        System.out.println("Listening... (sampleRate=" + sampleRate + ", frameLength=" + frameLength + ")");


        // Each sample is 16-bit (2 bytes). We read exactly one Porcupine frame at a time.
        byte[] byteBuffer = new byte[frameLength * 2];
        short[] pcm = new short[frameLength];

        while (true) {
            readFully(mic, byteBuffer, 0, byteBuffer.length);

            // Convert little-endian bytes -> signed 16-bit PCM samples
            ByteBuffer.wrap(byteBuffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(pcm);

            int keywordIndex = porcupine.process(pcm); // pass audio frame to Porcupine :contentReference[oaicite:2]{index=2}
            if (keywordIndex >= 0) {
                if (keywordIndex == 0) {
                    System.out.println("✅ Wake word detected: porcupine");
                } else if (keywordIndex == 1) {
                    System.out.println("✅ Wake word detected: bumblebee");
                } else {
                    System.out.println("✅ Wake word detected: index=" + keywordIndex);
                }
            }
        }
    }

    /**
     * Ensures we fill the buffer (TargetDataLine.read can return fewer bytes than requested).
     */
    private static void readFully(TargetDataLine line, byte[] buffer, int offset, int length) {
        int read = 0;
        while (read < length) {
            int r = line.read(buffer, offset + read, length - read);
            if (r < 0) {
                throw new IllegalStateException("Mic line closed while reading.");
            }
            read += r;
        }
    }
}
