package se.bahram.robotic.coversational_robot_test.usecases.transcribe_audio.applications;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import se.bahram.robotic.coversational_robot_test.usecases.transcribe_audio.applications.ports.in.TranscribeAudio;

import java.nio.file.Path;

@Service
public class TranscribeAudioService implements TranscribeAudio {

    private final OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;

    public TranscribeAudioService(OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel) {
        this.openAiAudioTranscriptionModel = openAiAudioTranscriptionModel;
    }

    @Override
    public String execute(Path audioFilePath) throws Exception {

        var audioFile = new FileSystemResource(audioFilePath);

        OpenAiAudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions
                .builder()
                .language("en")
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .build();

        var transcriptionRequest = new AudioTranscriptionPrompt(audioFile, transcriptionOptions);
        var response = openAiAudioTranscriptionModel.call(transcriptionRequest);

        return response.getResult().getOutput();
    }
}
