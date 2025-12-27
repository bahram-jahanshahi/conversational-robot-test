package se.bahram.robotic.coversational_robot_test.usecases.transcribe_audio.applications.ports.in;

public interface TranscribeAudio {

    String execute(String audioFilePath) throws Exception;
}
