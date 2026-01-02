package se.bahram.robotic.coversational_robot_test.usecases.describe_photo.applications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import se.bahram.robotic.coversational_robot_test.usecases.capture_photo.applications.ports.in.CapturePhotoViaPython;
import se.bahram.robotic.coversational_robot_test.usecases.capture_photo.domain.CapturedImage;
import se.bahram.robotic.coversational_robot_test.usecases.describe_photo.applications.ports.in.DescribePhotoByOpenAi;

import org.springframework.ai.chat.model.ChatModel;

import java.util.Base64;

@Service
@Slf4j
public class DescribePhotoByOpenAiService implements DescribePhotoByOpenAi {

    private final ChatClient chatClient;

    private final CapturePhotoViaPython capturePhotoViaPython;

    public DescribePhotoByOpenAiService(CapturePhotoViaPython capturePhotoViaPython, ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.capturePhotoViaPython = capturePhotoViaPython;
    }

    @Override
    public String execute(String questionAboutPhoto) throws Exception {

        log.info("Starting to describe photo by open AI");
        log.info("Capturing photo via python...");
        CapturedImage  capturedImage = this.capturePhotoViaPython.execute();
        log.info("Capturing photo via python done");
        log.info("Ask question about photo: {}", questionAboutPhoto);

        String response = chatClient.prompt()
                .user(u -> u.text(questionAboutPhoto)
                        .media(MimeTypeUtils.IMAGE_JPEG, new FileSystemResource(capturedImage.filePath())))
                .call()
                .content();
        log.info("The description of the photo is: {}", response);
        return response;
    }
}
