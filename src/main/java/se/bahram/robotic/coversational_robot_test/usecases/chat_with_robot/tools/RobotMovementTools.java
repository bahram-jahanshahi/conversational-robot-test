package se.bahram.robotic.coversational_robot_test.usecases.chat_with_robot.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import se.bahram.robotic.coversational_robot_test.usecases.move_robot.applications.MoveRobotService;
import se.bahram.robotic.coversational_robot_test.usecases.move_robot.domain.RobotMovementDirection;
import se.bahram.robotic.coversational_robot_test.usecases.move_robot.domain.RobotMovementTime;

@Service
@Slf4j
public record RobotMovementTools(MoveRobotService moveRobotService) {

    @Tool(description = "Stop the robot")
    public String stop(@ToolParam(description = "milliseconds of the action or movement of robot") long milliseconds) {
        log.info("Stopping robot");
        // Logic to stop the robot
        try {
            moveRobotService.execute(RobotMovementDirection.STOP, new RobotMovementTime(milliseconds));
            log.info("Robot stopped");
        } catch (Exception e) {
            log.error("Error while stopping robot", e);
            return "Failed to stop robot";
        }
        return "Robot stopped";
    }

    @Tool(description = "Move the robot forward")
    public String moveForward(@ToolParam(description = "milliseconds of the action or movement of robot") long milliseconds) {
        log.info("Moving robot forward");
        // Logic to move the robot forward
        try {
            moveRobotService.execute(RobotMovementDirection.FORWARD, new RobotMovementTime(milliseconds));
            log.info("Moved robot forward");
        } catch (Exception e) {
            log.error("Error while moving robot forward", e);
            return "Failed to move robot forward";
        }
        stop(0);
        return "Robot moved forward";
    }

    @Tool(description = "Move the robot backward")
    public String moveBackward(@ToolParam(description = "milliseconds of the action or movement of robot") long milliseconds) {
        log.info("Moving robot backward");
        // Logic to move the robot backward
        try {
            moveRobotService.execute(RobotMovementDirection.BACKWARD, new RobotMovementTime(milliseconds));
            log.info("Moved robot backward");
        } catch (Exception e) {
            log.error("Error while moving robot backward", e);
            return "Failed to move robot backward";
        }
        stop(0);
        return "Robot moved backward";
    }

    @Tool(description = "Turn the robot left")
    public String turnLeft(@ToolParam(description = "milliseconds of the action or movement of robot") long milliseconds) {
        log.info("Turning robot left");
        // Logic to turn the robot left
        try {
            moveRobotService.execute(RobotMovementDirection.LEFT, new RobotMovementTime(milliseconds));
            log.info("Turned robot left");
        } catch (Exception e) {
            log.error("Error while turning robot left", e);
            return "Failed to turn robot left";
        }
        stop(0);
        return "Robot turned left";
    }

    @Tool(description = "Turn the robot right")
    public String turnRight(@ToolParam(description = "milliseconds of the action or movement of robot") long milliseconds) {
        log.info("Turning robot right");
        // Logic to turn the robot right
        try {
            moveRobotService.execute(RobotMovementDirection.RIGHT, new RobotMovementTime(milliseconds));
            log.info("Turned robot right");
        } catch (Exception e) {
            log.error("Error while turning robot right", e);
            return "Failed to turn robot right";
        }
        stop(0);
        return "Robot turned right";
    }

    @Tool(description = "Rotate the robot left")
    public String rotateLeft(@ToolParam(description = "milliseconds of the action or movement of robot") long milliseconds) {
        log.info("Rotating robot left");
        // Logic to rotate the robot left
        try {
            moveRobotService.execute(RobotMovementDirection.ROTATE_LEFT, new RobotMovementTime(milliseconds));
            log.info("Rotated robot left");
        } catch (Exception e) {
            log.error("Error while rotating robot left", e);
            return "Failed to rotate robot left";
        }
        stop(0);
        return "Robot rotated left";
    }

    @Tool(description = "Rotate the robot right")
    public String rotateRight(@ToolParam(description = "milliseconds of the action or movement of robot") long milliseconds) {
        log.info("Rotating robot right");
        // Logic to rotate the robot right
        try {
            moveRobotService.execute(RobotMovementDirection.ROTATE_RIGHT, new RobotMovementTime(milliseconds));
            log.info("Rotated robot right");
        } catch (Exception e) {
            log.error("Error while rotating robot right", e);
            return "Failed to rotate robot right";
        }
        stop(0);
        return "Robot rotated right";
    }
}
