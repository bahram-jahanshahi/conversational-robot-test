package se.bahram.robotic.coversational_robot_test.usecases.wakeup_word.applications;

import ai.picovoice.porcupine.Porcupine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.bahram.robotic.coversational_robot_test.usecases.chat_with_robot.applications.ports.in.CommandRobot;
import se.bahram.robotic.coversational_robot_test.usecases.sound_player.applications.ports.in.WavPlayer;
import se.bahram.robotic.coversational_robot_test.usecases.text_to_speech.applications.TextToSpeechByOpenAiService;
import se.bahram.robotic.coversational_robot_test.usecases.transcribe_audio.applications.ports.in.TranscribeAudio;
import se.bahram.robotic.coversational_robot_test.usecases.voice_activity_detector.applications.services.VadMicRunner;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class PorcupineWakeWordService {

    @Value("${app.porcupine.api-key}")
    private String porcupineApiKey;

    @Value("${app.porcupine.hey-raz-keyword-file-path}")
    private String heyRazKeywordFilePath;

    @Value("${app.voice.i-am-listening-audio-file-path}")
    private String iAmListeningAudioFilePath;

    private final VadMicRunner runVadOnMic;

    private final WavPlayer wavPlayer;

    private final TranscribeAudio transcribeAudio;

    private final CommandRobot commandRobot;

    private final TextToSpeechByOpenAiService textToSpeechByOpenAiService;

    public PorcupineWakeWordService(VadMicRunner runVadOnMic, WavPlayer wavPlayer, TranscribeAudio transcribeAudio, CommandRobot commandRobot, TextToSpeechByOpenAiService textToSpeechByOpenAiService) {
        this.runVadOnMic = runVadOnMic;
        this.wavPlayer = wavPlayer;
        this.transcribeAudio = transcribeAudio;
        this.commandRobot = commandRobot;
        this.textToSpeechByOpenAiService = textToSpeechByOpenAiService;
    }

    public void execute() throws Exception {
        // Implementation for Porcupine Wake Word detection
        log.info("Waking up Porcupine WakeWord Service");
        // 1) Create Porcupine wake word engine
        log.info("Access Key: {}", porcupineApiKey);
        log.info("heyRazKeywordFilePath: {}", heyRazKeywordFilePath);
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
            if (keywordIndex == 0) {
                System.out.println("✅ Wake word detected: Hey Raz");

                wavPlayer.execute(Paths.get(iAmListeningAudioFilePath));
                // Optional: small drain so the wake-word audio doesn’t appear in the utterance
                drainMic(mic, sampleRate, 250); // 150ms

                Path wav = this.runVadOnMic.execute(mic);
                System.out.println("✅ Utterance recorded: " + wav);

                String transcription = this.transcribeAudio.execute(wav);
                System.out.println("📝 Transcription: " + transcription);

                var robotReply = commandRobot.execute(transcription);
                textToSpeechByOpenAiService.textToSpeechAndPlay(robotReply.reply());

                System.out.println("Waiting for the wake word...");
            }
        }
    }

    private static void drainMic(TargetDataLine mic, int sampleRate, int millis) {
        int bytesToRead = (int)((sampleRate * (millis / 1000.0)) * 2);
        byte[] trash = new byte[Math.min(bytesToRead, 8192)];
        int remaining = bytesToRead;
        while (remaining > 0) {
            int n = Math.min(trash.length, remaining);
            int r = mic.read(trash, 0, n);
            if (r <= 0) break;
            remaining -= r;
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
