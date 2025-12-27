package se.bahram.robotic.coversational_robot_test.usecases.chat_with_robot.applications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import se.bahram.robotic.coversational_robot_test.usecases.chat_with_robot.applications.ports.in.CommandRobot;
import se.bahram.robotic.coversational_robot_test.usecases.chat_with_robot.domain.RobotResponse;
import se.bahram.robotic.coversational_robot_test.usecases.chat_with_robot.tools.DescribeEnvironmentPhotoTools;
import se.bahram.robotic.coversational_robot_test.usecases.chat_with_robot.tools.RobotMovementTools;

@Service
@Slf4j
public class CommandRobotService implements CommandRobot {

    private final ChatClient chatClient;
    // tools
    private final RobotMovementTools robotMovementTools;
    private final DescribeEnvironmentPhotoTools describeEnvironmentPhotoTools;

    public CommandRobotService(@Qualifier("RobotChatClient") ChatClient chatClient, RobotMovementTools robotMovementTools, DescribeEnvironmentPhotoTools describeEnvironmentPhotoTools) {
        this.chatClient = chatClient;
        this.robotMovementTools = robotMovementTools;
        this.describeEnvironmentPhotoTools = describeEnvironmentPhotoTools;
    }

    @Override
    public RobotResponse execute(String command) throws Exception {
        log.info("Executing command: {}", command);

        if (command == null || command.trim().isBlank()) {
            return new RobotResponse("Command cannot be null or blank");
        }

        var reply = chatClient.prompt()
                .user(command)
                .tools(robotMovementTools, describeEnvironmentPhotoTools)
                .call()
                .entity(RobotResponse.class);

        log.info("Robot reply: {}", reply);
        return reply;
    }
}
