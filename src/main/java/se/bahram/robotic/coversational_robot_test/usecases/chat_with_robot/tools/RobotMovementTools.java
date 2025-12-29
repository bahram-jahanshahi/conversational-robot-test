package se.bahram.robotic.coversational_robot_test.usecases.chat_with_robot.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import se.bahram.robotic.coversational_robot_test.usecases.move_robot.applications.MoveRobotService;
import se.bahram.robotic.coversational_robot_test.usecases.move_robot.domain.RobotMovementDirection;

@Service
@Slf4j
public record RobotMovementTools(MoveRobotService moveRobotService) {

    @Tool(description = "Stop the robot")
    public String stop() {
        log.info("Stopping robot");
        // Logic to stop the robot
        try {
            moveRobotService.execute(RobotMovementDirection.STOP);
            log.info("Robot stopped");
        } catch (Exception e) {
            log.error("Error while stopping robot", e);
            return "Failed to stop robot";
        }
        // delayFiveSecondsAndStop();
        return "Robot stopped";
    }

    @Tool(description = "Move the robot forward")
    public String moveForward() {
        log.info("Moving robot forward");
        // Logic to move the robot forward
        try {
            moveRobotService.execute(RobotMovementDirection.FORWARD);
            log.info("Moved robot forward");
        } catch (Exception e) {
            log.error("Error while moving robot forward", e);
            return "Failed to move robot forward";
        }
        delayFiveSecondsAndStop();
        return "Robot moved forward";
    }

    @Tool(description = "Move the robot backward")
    public String moveBackward() {
        log.info("Moving robot backward");
        // Logic to move the robot backward
        try {
            moveRobotService.execute(RobotMovementDirection.BACKWARD);
            log.info("Moved robot backward");
        } catch (Exception e) {
            log.error("Error while moving robot backward", e);
            return "Failed to move robot backward";
        }
        delayFiveSecondsAndStop();
        return "Robot moved backward";
    }

    @Tool(description = "Turn the robot left")
    public String turnLeft() {
        log.info("Turning robot left");
        // Logic to turn the robot left
        try {
            moveRobotService.execute(RobotMovementDirection.LEFT);
            log.info("Turned robot left");
        } catch (Exception e) {
            log.error("Error while turning robot left", e);
            return "Failed to turn robot left";
        }
        delayFiveSecondsAndStop();
        return "Robot turned left";
    }

    @Tool(description = "Turn the robot right")
    public String turnRight() {
        log.info("Turning robot right");
        // Logic to turn the robot right
        try {
            moveRobotService.execute(RobotMovementDirection.RIGHT);
            log.info("Turned robot right");
        } catch (Exception e) {
            log.error("Error while turning robot right", e);
            return "Failed to turn robot right";
        }
        delayFiveSecondsAndStop();
        return "Robot turned right";
    }

    @Tool(description = "Rotate the robot left")
    public String rotateLeft() {
        log.info("Rotating robot left");
        // Logic to rotate the robot left
        try {
            moveRobotService.execute(RobotMovementDirection.ROTATE_LEFT);
            log.info("Rotated robot left");
        } catch (Exception e) {
            log.error("Error while rotating robot left", e);
            return "Failed to rotate robot left";
        }
        delayFiveSecondsAndStop();
        return "Robot rotated left";
    }

    @Tool(description = "Rotate the robot right")
    public String rotateRight() {
        log.info("Rotating robot right");
        // Logic to rotate the robot right
        try {
            moveRobotService.execute(RobotMovementDirection.ROTATE_RIGHT);
            log.info("Rotated robot right");
        } catch (Exception e) {
            log.error("Error while rotating robot right", e);
            return "Failed to rotate robot right";
        }
        delayFiveSecondsAndStop();
        return "Robot rotated right";
    }

    private void delayFiveSecondsAndStop() {
        delay(5000);
        try {
            stop();
            log.info("Robot stopped after delay");
        } catch (Exception e) {
            log.error("Error while stopping robot after delay", e);
        }
    }

    private void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Delay interrupted", e);
        }
    }
}
