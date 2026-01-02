package se.bahram.robotic.coversational_robot_test.usecases.chat_with_robot.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import se.bahram.robotic.coversational_robot_test.usecases.chat_with_robot.domain.RobotResponse;
import se.bahram.robotic.coversational_robot_test.usecases.describe_photo.applications.ports.in.DescribePhotoByOpenAi;

@Service
@Slf4j
public class DescribeEnvironmentPhotoTools {

    private final DescribePhotoByOpenAi describePhotoByOpenAi;

    public DescribeEnvironmentPhotoTools(DescribePhotoByOpenAi describePhotoByOpenAi) {
        this.describePhotoByOpenAi = describePhotoByOpenAi;
    }

    @Tool(description = "Take photo and ask your question about the environment in the photo")
    public RobotResponse describeEnvironmentPhoto(@ToolParam(description = "Ask your question about the objects in the photo") String questionAboutPhoto) {
        // Logic to analyze the photo and describe the environment
        try {
            var description = describePhotoByOpenAi.execute(questionAboutPhoto);
            //var description = "There is one apple and one orange in the environment.";
            log.info("Describing environment photo: {}", questionAboutPhoto);
            log.info("Description: {}", description);
            return new RobotResponse(description);
        } catch (Exception e) {
            log.error("Error while describing environment photo", e);
            return new RobotResponse("Failed to describe the environment photo");
        }
    }
}
