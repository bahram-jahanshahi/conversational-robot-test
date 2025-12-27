package se.bahram.robotic.coversational_robot_test.usecases.capture_photo.applications.ports.in;

import se.bahram.robotic.coversational_robot_test.usecases.capture_photo.domain.CapturedImage;

public interface CapturePhotoViaPython {
    CapturedImage execute() throws Exception;
}
