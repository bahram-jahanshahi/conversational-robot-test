package se.bahram.robotic.coversational_robot_test.usecases.chat_with_robot.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public record RobotMovementTools() {

    @Tool(description = "Move the robot forward")
    public String moveForward() {
        log.info("Moving robot forward");
        // Logic to move the robot forward
        return "Robot moved forward";
    }

    @Tool(description = "Move the robot backward")
    public String moveBackward() {
        log.info("Moving robot backward");
        // Logic to move the robot backward
        return "Robot moved backward";
    }

    @Tool(description = "Turn the robot left")
    public String turnLeft() {
        log.info("Turning robot left");
        // Logic to turn the robot left
        return "Robot turned left";
    }

    @Tool(description = "Turn the robot right")
    public String turnRight() {
        log.info("Turning robot right");
        // Logic to turn the robot right
        return "Robot turned right";
    }
}
