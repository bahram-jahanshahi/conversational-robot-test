package se.bahram.robotic.coversational_robot_test.usecases.move_robot.applications.ports.in;

import se.bahram.robotic.coversational_robot_test.usecases.move_robot.domain.RobotMovementDirection;

public interface MoveRobot {

    void execute(RobotMovementDirection robotMovementDirection) throws Exception;
}
