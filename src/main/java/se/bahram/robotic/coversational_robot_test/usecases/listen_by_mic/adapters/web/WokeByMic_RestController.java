package se.bahram.robotic.coversational_robot_test.usecases.listen_by_mic.adapters.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.bahram.robotic.coversational_robot_test.usecases.listen_by_mic.applications.ports.in.WokeByMic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/robot")
public class WokeByMic_RestController {

    private final Path inboxDir = Path.of("./inbox-audio").toAbsolutePath();

    private final WokeByMic wokeByMic;

    public WokeByMic_RestController(WokeByMic wokeByMic) throws IOException {
        this.wokeByMic = wokeByMic;
    }

    @PostMapping(value = "/woke", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> woke(
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String ts
    ) {

        try {
            this.wokeByMic.execute(file);
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Error processing file: " + ex.getMessage());
        }

        return ResponseEntity.ok("Received");
    }
}
