package se.bahram.robotic.coversational_robot_test.usecases.move_robot.applications;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import se.bahram.robotic.coversational_robot_test.usecases.move_robot.applications.ports.in.MoveRobot;
import se.bahram.robotic.coversational_robot_test.usecases.move_robot.domain.RobotMovementDirection;
import se.bahram.robotic.coversational_robot_test.usecases.move_robot.domain.RobotMovementTime;

@Service
public class MoveRobotService implements MoveRobot {

    private final RestClient restClient = RestClient.create();

    private final boolean movingEnabled = true;

    @Override
    public void execute(RobotMovementDirection robotMovementDirection, RobotMovementTime movingTime) throws Exception {
        System.out.println("Executing robot movement: " + robotMovementDirection + " for " + movingTime.milliSeconds() + " ms");
        switch (robotMovementDirection) {
            case STOP -> handleStop(movingTime);
            case FORWARD -> handleForward(movingTime);
            case BACKWARD -> handleBackward(movingTime);
            case RIGHT -> handleRight(movingTime);
            case LEFT -> handleLeft(movingTime);
            case ROTATE_RIGHT -> handleRotateRight(movingTime);
            case ROTATE_LEFT -> handleRotateLeft(movingTime);
            default -> handleStop(movingTime);

        }
    }

    private void handleStop(RobotMovementTime time) throws Exception {
        if (!movingEnabled) return;
        restClient.post()
                .uri("http://127.0.0.1:5000/stop")
                .retrieve()
                .toBodilessEntity();
        delayMilliseconds(time.milliSeconds());
    }

    private void handleForward(RobotMovementTime time) throws Exception {
        if (!movingEnabled) return;
        restClient.post()
                .uri("http://127.0.0.1:5000/move/forward")
                .retrieve()
                .toBodilessEntity();
        delayMilliseconds(time.milliSeconds());
        handleStop(new RobotMovementTime(0));
    }

    private void handleBackward(RobotMovementTime time) throws Exception {
        if (!movingEnabled) return;
        long start = System.currentTimeMillis();
        restClient.post()
                .uri("http://127.0.0.1:5000/move/backward")
                .retrieve()
                .toBodilessEntity();
        delayMilliseconds(time.milliSeconds());
    }

    private void handleRight(RobotMovementTime time) throws Exception {
        if (!movingEnabled) return;
        restClient.post()
                .uri("http://127.0.0.1:5000/move/right")
                .retrieve()
                .toBodilessEntity();
        delayMilliseconds(time.milliSeconds());
    }

    private void handleLeft(RobotMovementTime time) throws Exception {
        if (!movingEnabled) return;
        restClient.post()
                .uri("http://127.0.0.1:5000/move/left")
                .retrieve()
                .toBodilessEntity();
        delayMilliseconds(time.milliSeconds());
    }

    private void handleRotateRight(RobotMovementTime time) throws Exception {
        if (!movingEnabled) return;
        restClient.post()
                .uri("http://127.0.0.1:5000/rotate/right")
                .retrieve()
                .toBodilessEntity();
    }

    private void handleRotateLeft(RobotMovementTime time) throws Exception {
        if (!movingEnabled) return;
        restClient.post()
                .uri("http://127.0.0.1:5000/rotate/left")
                .retrieve()
                .toBodilessEntity();
        delayMilliseconds(time.milliSeconds());
    }

    private void delayMilliseconds(long milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
