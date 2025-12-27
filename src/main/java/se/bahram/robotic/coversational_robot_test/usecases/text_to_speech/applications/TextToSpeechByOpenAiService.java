package se.bahram.robotic.coversational_robot_test.usecases.text_to_speech.applications;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.metadata.audio.OpenAiAudioSpeechResponseMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.bahram.robotic.coversational_robot_test.usecases.text_to_speech.domain.SavedAudio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class TextToSpeechByOpenAiService {

    private final OpenAiAudioSpeechModel openAiAudioSpeechModel;

    @Value("${app.upload.directory}")
    private String uploadDir;

    public TextToSpeechByOpenAiService(OpenAiAudioSpeechModel openAiAudioSpeechModel) {
        this.openAiAudioSpeechModel = openAiAudioSpeechModel;
    }

    public byte[] textToSpeech(String text) throws Exception {

        var speechOptions = OpenAiAudioSpeechOptions.builder()
                .voice(OpenAiAudioApi.SpeechRequest.Voice.ECHO)
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .speed(1.0)
                .model(OpenAiAudioApi.TtsModel.TTS_1.value)
                .build();

        var speechPrompt = new TextToSpeechPrompt(text, speechOptions);
        var response = openAiAudioSpeechModel.call(speechPrompt);

        // Accessing metadata (rate limit info)
        //OpenAiAudioSpeechResponseMetadata metadata = response.getMetadata();
        byte[] responseAsBytes = response.getResult().getOutput();

        return responseAsBytes;
    }

    public SavedAudio saveAudio(byte[] audio, String fileName) throws Exception {
        String fileNameWithPath = uploadDir + "/" + fileName;
        File file = new File(fileNameWithPath);
        // Write bytes to file
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(audio);

        return new SavedAudio(fileNameWithPath, audio);
    }

    public void playAudio(String fileName) throws Exception {
        playMP3(fileName);
    }

    public void textToSpeechAndSave(String text, String fileName) throws Exception {
        byte[] audio = textToSpeech(text);
        saveAudio(audio, fileName);
    }

    public static void playMP3(String fileName) throws IOException, JavaLayerException {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            Player player = new Player(fis);
            player.play();
        }
    }
}
