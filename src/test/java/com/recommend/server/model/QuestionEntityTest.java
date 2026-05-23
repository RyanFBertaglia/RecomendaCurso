package com.recommend.server.model;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuestionEntityTest {

    @Test @Order(1)
    @DisplayName("Question deve ter id, text e lista de alternativas")
    void testQuestionFields() {
        Question question = new Question();
        question.setId(1);
        question.setText("Você prefere liderar decisões?");

        Alternative altD = new Alternative();
        altD.setId(1);
        altD.setText("Sim");
        altD.setDimension('D');
        altD.setWeight(1.0);
        altD.setQuestion(question);

        Alternative altI = new Alternative();
        altI.setId(2);
        altI.setText("Não");
        altI.setDimension('I');
        altI.setWeight(0.5);
        altI.setQuestion(question);

        question.setAlternatives(List.of(altD, altI));

        assertEquals(1, question.getId());
        assertEquals("Você prefere liderar decisões?", question.getText());
        assertEquals(2, question.getAlternatives().size());
    }

    @Test @Order(2)
    @DisplayName("Alternative deve ter id, text, dimension, weight e relação com Question")
    void testAlternativeFields() {
        Question question = new Question();
        question.setId(1);
        question.setText("Pergunta teste");

        Alternative alt = new Alternative();
        alt.setId(1);
        alt.setText("Sim");
        alt.setDimension('D');
        alt.setWeight(1.0);
        alt.setQuestion(question);

        assertEquals(1, alt.getId());
        assertEquals("Sim", alt.getText());
        assertEquals('D', alt.getDimension());
        assertEquals(1.0, alt.getWeight(), 1e-9);
        assertEquals(question, alt.getQuestion());
    }

    @Test @Order(3)
    @DisplayName("getAlternative deve retornar alternativa por id")
    void testGetAlternativeById() {
        Question question = new Question();
        question.setId(1);
        question.setText("Você prefere liderar decisões?");

        Alternative altD = new Alternative();
        altD.setId(1);
        altD.setText("Sim");
        altD.setDimension('D');
        altD.setWeight(1.0);
        altD.setQuestion(question);

        Alternative altI = new Alternative();
        altI.setId(2);
        altI.setText("Não");
        altI.setDimension('I');
        altI.setWeight(0.5);
        altI.setQuestion(question);

        question.setAlternatives(List.of(altD, altI));

        Alternative found = question.getAlternative(1);
        assertNotNull(found);
        assertEquals('D', found.getDimension());

        Alternative notFound = question.getAlternative(99);
        assertNull(notFound);
    }

    @Test @Order(4)
    @DisplayName("Construtor com parâmetros deve funcionar")
    void testQuestionConstructor() {
        Alternative altD = new Alternative(1, "Sim", 'D', 1.0, null);
        Alternative altI = new Alternative(2, "Não", 'I', 0.5, null);

        Question question = new Question(1, "Você prefere liderar decisões?", List.of(altD, altI));

        assertEquals(1, question.getId());
        assertEquals(2, question.getAlternatives().size());
        assertEquals('D', question.getAlternatives().get(0).getDimension());
    }
}
