package se.bahram.robotic.coversational_robot_test.usecases.sound_player.applications;

import org.springframework.stereotype.Service;
import se.bahram.robotic.coversational_robot_test.usecases.sound_player.applications.ports.in.WavPlayer;

import java.nio.file.Path;
import javax.sound.sampled.*;
import java.io.File;
import java.nio.file.Path;

@Service
public class WavPlayerService implements WavPlayer {
    @Override
    public void execute(Path wavFilePath) throws Exception {
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(wavFilePath.toFile())) {

            AudioFormat baseFormat = ais.getFormat();

            // Ensure format is playable (PCM_SIGNED)
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
            );

            try (AudioInputStream decodedAis =
                         AudioSystem.getAudioInputStream(decodedFormat, ais)) {

                DataLine.Info info = new DataLine.Info(Clip.class, decodedFormat);
                Clip clip = (Clip) AudioSystem.getLine(info);

                clip.open(decodedAis);
                clip.start();

                // Wait until playback finishes
                while (clip.isRunning()) {
                    Thread.sleep(10);
                }

                clip.close();
            }
        }
    }
}
