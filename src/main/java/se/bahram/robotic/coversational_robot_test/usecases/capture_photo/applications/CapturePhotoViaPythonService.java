package se.bahram.robotic.coversational_robot_test.usecases.capture_photo.applications;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import se.bahram.robotic.coversational_robot_test.usecases.capture_photo.applications.ports.in.CapturePhotoViaPython;
import se.bahram.robotic.coversational_robot_test.usecases.capture_photo.domain.CapturedImage;

import java.io.File;


@Service
public class CapturePhotoViaPythonService implements CapturePhotoViaPython {

    private static final String BASE_URL = "http://127.0.0.1:8000";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.upload.directory}")
    String uploadDirectory;

    public CapturedImage execute() {

        String url = UriComponentsBuilder
                .fromHttpUrl(BASE_URL)
                .path("/capture")
                .queryParam("device_index", 0)
                .queryParam("width", 1280)
                .queryParam("height", 720)
                .queryParam("quality", 90)
                .build()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(MediaType.parseMediaTypes(MediaType.IMAGE_JPEG_VALUE));

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                byte[].class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("Failed to capture image from Python webcam service");
        }

        var capturedImagePath = uploadDirectory + "/captured_image_python.jpg";

        try {
            var imageBytes = response.getBody();
            File outputFile = new File(capturedImagePath);
            java.nio.file.Files.write(outputFile.toPath(), imageBytes);
            return new CapturedImage(capturedImagePath, imageBytes);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to save captured image to disk", exception);
        }
    }
}
