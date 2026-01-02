package se.bahram.robotic.coversational_robot_test.usecases.sound_player.applications.ports.in;

import java.nio.file.Path;

public interface WavPlayer {

    void execute(Path wavFilePath) throws Exception;
}
