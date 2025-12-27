package se.bahram.robotic.coversational_robot_test.usecases.capture_photo.applications;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.javacv.JavaCvDriver;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import se.bahram.robotic.coversational_robot_test.usecases.capture_photo.applications.ports.in.CapturePhotoByWebcam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
public class CapturePhotoByWebcamService implements CapturePhotoByWebcam {

    /**
     * IMPORTANT:
     * This must run BEFORE any call to Webcam.getDefault() / Webcam.getWebcams().
     */
    @PostConstruct
    public void initWebcamDriver() {
        Webcam.setDriver(new JavaCvDriver());
    }

    @Override
    public byte[] execute() throws Exception{

        Webcam webcam = Webcam.getDefault();
        if (webcam == null) {
            throw new Exception("No webcam detected");
        }

        try {
            webcam.open();
            // Capture the image
            BufferedImage image = webcam.getImage();
            if (image == null) {
                throw new Exception("Failed to capture image from webcam");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);

            return out.toByteArray();
        } finally {
            if (webcam.isOpen()) {
                webcam.close();
            }
        }
    }
}
