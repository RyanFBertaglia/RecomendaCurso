package com.recommend.server.ia;

import com.recommend.server.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ChatService.class)
public class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @Test
    public void testAnswerQuestions() {
        String question = "Como me é recomendado um curso?";
        String response = chatService.respond(question);
        assertEquals("O sistema compara suas habilidades com os cursos.", response);
        System.out.println(response);
    }
}
