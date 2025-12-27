package se.bahram.robotic.coversational_robot_test.utilities;

import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class AiAgentPersona {

    public static final String ROBOT_PERSONA = "classpath:ai_persona/robot-persona.txt";

    private final ResourceLoader resourceLoader;

    public AiAgentPersona(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String getPersonaContent(String persona) {
        var resource = resourceLoader.getResource(persona);
        try {
            return new String(resource.getInputStream().readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load persona content from: " + persona, e);
        }
    }
}
