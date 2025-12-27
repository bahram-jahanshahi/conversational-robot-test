package se.bahram.robotic.coversational_robot_test.usecases.move_robot.applications;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import se.bahram.robotic.coversational_robot_test.usecases.move_robot.applications.ports.in.MoveRobot;
import se.bahram.robotic.coversational_robot_test.usecases.move_robot.domain.RobotMovementDirection;

@Service
public class MoveRobotService implements MoveRobot {

    private final RestClient restClient = RestClient.create();

    @Override
    public void execute(RobotMovementDirection robotMovementDirection) throws Exception {
        if (robotMovementDirection == RobotMovementDirection.STOP) {
            restClient.post()
                    .uri("http://127.0.0.1:5000/stop")
                    .retrieve()
                    .toBodilessEntity();
        }
        if (robotMovementDirection == RobotMovementDirection.FORWARD) {
            restClient.post()
                    .uri("http://127.0.0.1:5000/move/forward")
                    .retrieve()
                    .toBodilessEntity();
        }
        if (robotMovementDirection == RobotMovementDirection.BACKWARD) {
            restClient.post()
                    .uri("http://127.0.0.1:5000/move/backward")
                    .retrieve()
                    .toBodilessEntity();
        }
        if (robotMovementDirection == RobotMovementDirection.RIGHT) {
            restClient.post()
                    .uri("http://127.0.0.1:5000/move/right")
                    .retrieve()
                    .toBodilessEntity();
        }
        if (robotMovementDirection == RobotMovementDirection.LEFT) {
            restClient.post()
                    .uri("http://127.0.0.1:5000/move/left")
                    .retrieve()
                    .toBodilessEntity();
        }
        if (robotMovementDirection == RobotMovementDirection.ROTATE_RIGHT) {
            restClient.post()
                    .uri("http://127.0.0.1:5000/rotate/right")
                    .retrieve()
                    .toBodilessEntity();
        }
        if (robotMovementDirection == RobotMovementDirection.ROTATE_LEFT) {
            restClient.post()
                    .uri("http://127.0.0.1:5000/rotate/left")
                    .retrieve()
                    .toBodilessEntity();
        }
    }
}
