package se.bahram.robotic.coversational_robot_test.usecases.describe_photo.applications;

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
public class DescribePhotoByOpenAiService implements DescribePhotoByOpenAi {

    private final ChatClient chatClient;

    private final CapturePhotoViaPython capturePhotoViaPython;

    public DescribePhotoByOpenAiService(CapturePhotoViaPython capturePhotoViaPython, ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.capturePhotoViaPython = capturePhotoViaPython;
    }

    @Override
    public String execute() throws Exception {

        CapturedImage  capturedImage = this.capturePhotoViaPython.execute();


        String response = chatClient.prompt()
                .user(u -> u.text("Tell me what you see in this image.")
                        .media(MimeTypeUtils.IMAGE_JPEG, new FileSystemResource(capturedImage.filePath())))
                .call()
                .content();


        return response;
    }
}
