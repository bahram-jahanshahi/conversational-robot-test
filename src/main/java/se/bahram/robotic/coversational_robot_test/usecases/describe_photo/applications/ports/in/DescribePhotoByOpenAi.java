package se.bahram.robotic.coversational_robot_test.usecases.describe_photo.applications.ports.in;

import se.bahram.robotic.coversational_robot_test.usecases.capture_photo.domain.CapturedImage;

public interface DescribePhotoByOpenAi {

    String execute(String questionAboutPhoto) throws Exception;
}
