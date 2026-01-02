// java
package se.bahram.robotic.coversational_robot_test.usecases.sound_player.applications;

import org.springframework.stereotype.Service;
import se.bahram.robotic.coversational_robot_test.usecases.sound_player.applications.ports.in.WavPlayer;

import java.nio.file.Path;
import javax.sound.sampled.*;

@Service
public class WavPlayerService implements WavPlayer {
    @Override
    public void execute(Path wavFilePath) throws Exception {
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(wavFilePath.toFile())) {

            AudioFormat baseFormat = ais.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
            );

            try (AudioInputStream decodedAis = AudioSystem.getAudioInputStream(decodedFormat, ais)) {
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
                try (SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
                    line.open(decodedFormat);
                    line.start();

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = decodedAis.read(buffer, 0, buffer.length)) != -1) {
                        line.write(buffer, 0, bytesRead);
                    }

                    line.drain();
                    line.stop();
                }
            }
        }
    }
}