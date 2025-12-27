package se.bahram.robotic.coversational_robot_test.usecases.chat_with_robot.configs;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.bahram.robotic.coversational_robot_test.utilities.AiAgentPersona;

@Configuration
public class RobotChatConfig {

    private final AiAgentPersona aiAgentPersona;

    public RobotChatConfig(AiAgentPersona aiAgentPersona) {
        this.aiAgentPersona = aiAgentPersona;
    }

    @Bean(name = "RobotChatClient")
    ChatClient chatClient(ChatClient.Builder builder) {

        return builder
                .defaultSystem(aiAgentPersona.getPersonaContent(AiAgentPersona.ROBOT_PERSONA))
                .build();
    }
}
