package se.bahram.robotic.coversational_robot_test.usecases.voice_activity_detector.applications.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sound.sampled.TargetDataLine;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

@Service
public class VadMicRunner {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    @Value("${app.silero.vad-model-path}")
    private String sileroVadModelPath;

    @Value("${app.voice.recording-directory}")
    private String voiceRecordingDirectory;

    public Path execute(TargetDataLine mic) throws Exception {
        Path model = Paths.get(sileroVadModelPath);

        try (SileroVadOnnxModel vad = new SileroVadOnnxModel(model)) {

            Endpointing ep = new Endpointing(
                    0.65f,  // startTh
                    0.40f,  // endTh
                    0.10,   // start after ~200ms voiced
                    2.00,   // end after 2s unvoiced ✅
                    SileroVadOnnxModel.WINDOW_SAMPLES, // 512
                    SileroVadOnnxModel.SAMPLE_RATE     // 16000
            );

            UtteranceBuffer buf = new UtteranceBuffer(12); // 12 frames ~ 384ms pre-roll
            buf.clearPreRoll();
            vad.reset();
            Path outDir = Paths.get(voiceRecordingDirectory);
            Files.createDirectories(outDir);

            byte[] framePcm = new byte[SileroVadOnnxModel.WINDOW_SAMPLES * 2]; // 512 samples * 2 bytes

            // warm up pre-roll for ~300ms before allowing SPEECH_START
            int warmupFrames = 10; // 10*32ms = 320ms
            for (int i = 0; i < warmupFrames; i++) {
                readFully(mic, framePcm, 0, framePcm.length);
                buf.addToPreRoll(framePcm);
            }

            System.out.println("🎙️ VAD capturing utterance...");
            while (true) {
                readFully(mic, framePcm, 0, framePcm.length);

                if (!buf.isRecording()) {
                    buf.addToPreRoll(framePcm);
                }

                // VAD
                float[] frameFloat = Mic16kMono.pcm16leToFloat(framePcm);
                float p = vad.infer(frameFloat);

                var ev = ep.update(p);

                if (ev == Endpointing.Event.SPEECH_START) {
                    System.out.println(">>> SPEECH START");
                    buf.start();
                }

                if (buf.isRecording()) {
                    buf.append(framePcm);
                }

                if (ev == Endpointing.Event.SPEECH_END) {
                    System.out.println("<<< SPEECH END");

                    byte[] utterancePcm = buf.stopAndGetPcm();
                    double seconds = utterancePcm.length / 2.0 / SileroVadOnnxModel.SAMPLE_RATE;

                    vad.reset();

                    if (seconds < 0.5) {
                        System.out.println("Discarded short utterance: " + seconds + "s");
                        // keep listening for a real one
                        continue;
                    }

                    String fileName = "utt_" + TS.format(LocalDateTime.now()) + ".wav";
                    Path wavPath = outDir.resolve(fileName);

                    WavWriter.writePcm16leMonoWav(wavPath, utterancePcm, SileroVadOnnxModel.SAMPLE_RATE);
                    System.out.println("Saved: " + wavPath.toAbsolutePath() + " seconds=" + seconds);

                    return wavPath;
                }
            }
        }
    }

    private static void readFully(TargetDataLine line, byte[] buffer, int offset, int length) {
        int read = 0;
        while (read < length) {
            int r = line.read(buffer, offset + read, length - read);
            if (r < 0) throw new IllegalStateException("Mic line closed while reading.");
            read += r;
        }
    }


}
