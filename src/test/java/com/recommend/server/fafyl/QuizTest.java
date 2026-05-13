package com.recommend.server.fafyl;

import com.recommend.server.model.Alternative;
import com.recommend.server.model.Question;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

public class QuizTest {

    private Question quiz;

    void setUp() {
        Alternative D = new Alternative(1, "Sim", 'D', 1);
        Alternative I = new Alternative(2, "Não", 'I', 1);
        Alternative S = new Alternative(3, "Sim", 'S', 1);
        Alternative C = new Alternative(4, "Não", 'C', 1);
        quiz = new Question(1, "Question", List.of(D, I, S, C));

    }

    @Test
    void testQuiz() {
        setUp();
        int choose = 1;
        IO.println(quiz.getAlternative(choose));
    }
}
