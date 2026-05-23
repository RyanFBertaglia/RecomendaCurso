package com.recommend.server.service;

import com.recommend.server.dto.AlternativeDTO;
import com.recommend.server.dto.QuestionDTO;
import com.recommend.server.model.Alternative;
import com.recommend.server.model.Question;
import com.recommend.server.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataServiceQuestionsTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private DataService dataService;

    @Test
    void shouldInsertQuestionsWithAlternatives() {
        AlternativeDTO altD = new AlternativeDTO("Sim", 'D', 1.0);
        AlternativeDTO altI = new AlternativeDTO("Não", 'I', 0.5);
        QuestionDTO dto = new QuestionDTO("Você prefere liderar decisões?", List.of(altD, altI));

        when(questionRepository.saveAll(any())).thenAnswer(invocation -> {
            List<Question> questions = invocation.getArgument(0);
            for (int i = 0; i < questions.size(); i++) {
                questions.get(i).setId(i + 1);
            }
            return questions;
        });

        List<Question> result = dataService.insertQuestions(List.of(dto));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getText()).isEqualTo("Você prefere liderar decisões?");
        assertThat(result.get(0).getAlternatives()).hasSize(2);
        assertThat(result.get(0).getAlternatives().get(0).getDimension()).isEqualTo('D');
        assertThat(result.get(0).getAlternatives().get(0).getWeight()).isEqualTo(1.0);

        verify(questionRepository).saveAll(any());
    }

    @Test
    void shouldInsertMultipleQuestions() {
        QuestionDTO q1 = new QuestionDTO("Pergunta 1?", List.of(
                new AlternativeDTO("Sim", 'D', 1.0)
        ));
        QuestionDTO q2 = new QuestionDTO("Pergunta 2?", List.of(
                new AlternativeDTO("Não", 'S', 0.8)
        ));

        when(questionRepository.saveAll(any())).thenAnswer(invocation -> {
            List<Question> questions = invocation.getArgument(0);
            for (int i = 0; i < questions.size(); i++) {
                questions.get(i).setId(i + 1);
            }
            return questions;
        });

        List<Question> result = dataService.insertQuestions(List.of(q1, q2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getText()).isEqualTo("Pergunta 1?");
        assertThat(result.get(1).getText()).isEqualTo("Pergunta 2?");
    }
}
