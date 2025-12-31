package se.bahram.robotic.coversational_robot_test;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import se.bahram.robotic.coversational_robot_test.usecases.capture_photo.applications.ports.in.CapturePhotoViaPython;
import se.bahram.robotic.coversational_robot_test.usecases.describe_photo.applications.ports.in.DescribePhotoByOpenAi;
import se.bahram.robotic.coversational_robot_test.usecases.text_to_speech.applications.TextToSpeechByOpenAiService;
import se.bahram.robotic.coversational_robot_test.usecases.text_to_speech.domain.SavedAudio;
import se.bahram.robotic.coversational_robot_test.usecases.wakeup_word.applications.PorcupineWakeWordService;

import javax.sound.sampled.AudioSystem;


@SpringBootApplication
@EnableScheduling
public class CoversationalRobotTestApplication implements ApplicationRunner {

    private final CapturePhotoViaPython capturePhotoViaPython;
    private final DescribePhotoByOpenAi describePhotoByOpenAi;
    private final TextToSpeechByOpenAiService textToSpeechByOpenAiService;
    private final PorcupineWakeWordService porcupineWakeWordService;

    public CoversationalRobotTestApplication(CapturePhotoViaPython capturePhotoViaPython, DescribePhotoByOpenAi describePhotoByOpenAi, TextToSpeechByOpenAiService textToSpeechByOpenAiService, PorcupineWakeWordService porcupineWakeWordService) {
        this.capturePhotoViaPython = capturePhotoViaPython;
        this.describePhotoByOpenAi = describePhotoByOpenAi;
        this.textToSpeechByOpenAiService = textToSpeechByOpenAiService;
        this.porcupineWakeWordService = porcupineWakeWordService;
    }

    public static void main(String[] args) {
		SpringApplication.run(CoversationalRobotTestApplication.class, args);
	}

    @Override
    public void run(ApplicationArguments args) throws Exception {

        for (var mi : AudioSystem.getMixerInfo()) {
            var m = AudioSystem.getMixer(mi);
            if (m.getTargetLineInfo().length > 0) {
                //System.out.println(mi.getName() + " | " + mi.getDescription());
            }
        }

        this.porcupineWakeWordService.execute();
    }

    //@Scheduled(fixedDelay = 10000)
    public void capturePhoto() throws Exception {
        String description = describePhotoByOpenAi.execute();
        System.out.println("Photo description: " + description);

        byte[] audioBytes = textToSpeechByOpenAiService.textToSpeech(description);
        SavedAudio savedAudio = textToSpeechByOpenAiService.saveAudio(audioBytes, "photo_description.mp3");
        textToSpeechByOpenAiService.playAudio(savedAudio.fileName());
    }
}
