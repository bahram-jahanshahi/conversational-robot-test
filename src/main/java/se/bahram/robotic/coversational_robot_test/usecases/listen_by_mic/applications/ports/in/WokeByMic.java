package se.bahram.robotic.coversational_robot_test.usecases.listen_by_mic.applications.ports.in;

import org.springframework.web.multipart.MultipartFile;

public interface WokeByMic {

    void execute(MultipartFile file) throws Exception;
}
