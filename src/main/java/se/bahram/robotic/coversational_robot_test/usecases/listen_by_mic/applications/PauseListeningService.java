package se.bahram.robotic.coversational_robot_test.usecases.listen_by_mic.applications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import se.bahram.robotic.coversational_robot_test.usecases.listen_by_mic.applications.ports.in.PauseListening;

@Service
@Slf4j
public class PauseListeningService implements PauseListening {

    private final RestClient restClient = RestClient.create();

    @Override
    public void execute() throws Exception {
        restClient.post()
                .uri("http://127.0.0.1:5055/listen/pause")
                .retrieve()
                .toBodilessEntity();

        log.info("Listening paused.");
    }
}
