package se.bahram.robotic.coversational_robot_test.usecases.move_robot.applications.ports.in;

import se.bahram.robotic.coversational_robot_test.usecases.move_robot.domain.RobotMovementDirection;
import se.bahram.robotic.coversational_robot_test.usecases.move_robot.domain.RobotMovementTime;

import java.util.TimeZone;

public interface MoveRobot {

    void execute(RobotMovementDirection robotMovementDirection, RobotMovementTime milliSeconds) throws Exception;
}
