package se.bahram.robotic.coversational_robot_test.usecases.chat_with_robot.applications.ports.in;

import se.bahram.robotic.coversational_robot_test.usecases.chat_with_robot.domain.RobotResponse;

public interface CommandRobot {

    RobotResponse execute(String command) throws Exception;
}
