package se.bahram.robotic.coversational_robot_test.usecases.voice_activity_detector.applications.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public void execute() throws Exception{
        Path model = Paths.get(sileroVadModelPath);

        try (SileroVadOnnxModel vad = new SileroVadOnnxModel(model);
             Mic16kMono mic = new Mic16kMono(SileroVadOnnxModel.SAMPLE_RATE)) {

            // You will tune these, but this is a good starting point
            Endpointing ep = new Endpointing(
                    0.60f,  // start threshold
                    0.35f,  // end threshold
                    0.20,   // require ~200ms of speech to start (prevents clicks)
                    2.00,   // require 2 seconds of non-speech to end  ✅
                    SileroVadOnnxModel.WINDOW_SAMPLES,  // 512
                    SileroVadOnnxModel.SAMPLE_RATE      // 16000
            );

            UtteranceBuffer buf = new UtteranceBuffer(10);

            Path outDir = Paths.get(voiceRecordingDirectory);
            Files.createDirectories(outDir);// ~320ms pre-roll

            System.out.println("Listening... (Ctrl+C to stop)");
            while (true) {
                // read frame PCM
                byte[] framePcm = mic.readPcmBytes(SileroVadOnnxModel.WINDOW_SAMPLES);

                // keep pre-roll always
                buf.addToPreRoll(framePcm);

                // run VAD on float conversion
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

                    if (seconds < 0.5) { // 500ms
                        System.out.println("Discarded short utterance: " + seconds + "s");
                        continue;
                    }

                    // (Optional) reset VAD state between utterances
                    vad.reset();

                    // Write WAV file
                    String fileName = "utt_" + TS.format(LocalDateTime.now()) + ".wav";
                    Path wavPath = outDir.resolve(fileName);

                    WavWriter.writePcm16leMonoWav(wavPath, utterancePcm, SileroVadOnnxModel.SAMPLE_RATE);

                    System.out.println("Saved: " + wavPath.toAbsolutePath() + " bytes=" + utterancePcm.length);
                }

                // debug
                // System.out.printf("p=%.3f inSpeech=%s%n", p, ep.isInSpeech());
            }
        }
    }
}
