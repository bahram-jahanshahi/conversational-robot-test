package se.bahram.robotic.coversational_robot_test.usecases.transcribe_audio.applications.ports.in;

import java.nio.file.Path;

public interface TranscribeAudio {

    String execute(Path audioFilePath) throws Exception;
}
