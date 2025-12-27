package se.bahram.robotic.coversational_robot_test.usecases.listen_by_mic.applications;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import se.bahram.robotic.coversational_robot_test.usecases.listen_by_mic.applications.ports.in.WokeByMic;
import se.bahram.robotic.coversational_robot_test.usecases.transcribe_audio.applications.ports.in.TranscribeAudio;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class WokeByMicService implements WokeByMic {

    private final TranscribeAudio transcribeAudio;

    @Value("${app.upload.directory}")
    String uploadDirectory;

    public WokeByMicService(TranscribeAudio transcribeAudio) {
        this.transcribeAudio = transcribeAudio;
    }

    @Override
    public void execute(MultipartFile file) throws Exception {

        Path inboxDir = Path.of(uploadDirectory, "inbox-audio").toAbsolutePath();
        Files.createDirectories(inboxDir);

        //String safeName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        String safeName = "woke-audio.wav";
        Path out = inboxDir.resolve(safeName);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
        }

        // Next step: send 'out' to STT, then agent, then TTS
        // sttService.transcribe(out) ...
        var transcription = this.transcribeAudio.execute(out.toFile().getAbsolutePath());

        System.out.println("Transcription: " + transcription);
    }
}
