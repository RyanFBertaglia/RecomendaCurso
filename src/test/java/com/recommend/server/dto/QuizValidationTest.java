package com.recommend.server.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuizValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test @Order(1)
    @DisplayName("Deve falhar quando discProfile é null")
    void shouldFailWhenDiscProfileIsNull() {
        Quiz quiz = new Quiz(null);
        Set<ConstraintViolation<Quiz>> violations = validator.validate(quiz);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("discProfile")));
    }

    @Test @Order(2)
    @DisplayName("Deve falhar quando discProfile é vazio")
    void shouldFailWhenDiscProfileIsEmpty() {
        Quiz quiz = new Quiz(Collections.emptyMap());
        Set<ConstraintViolation<Quiz>> violations = validator.validate(quiz);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("discProfile")));
    }

    @Test @Order(3)
    @DisplayName("Deve passar quando discProfile tem todas as chaves DISC")
    void shouldPassWhenDiscProfileIsComplete() {
        Quiz quiz = new Quiz(Map.of('D', 1.0, 'I', 0.5, 'S', 0.8, 'C', 0.2));
        Set<ConstraintViolation<Quiz>> violations = validator.validate(quiz);

        assertTrue(violations.isEmpty());
    }

    @Test @Order(4)
    @DisplayName("Deve passar quando discProfile tem subset de chaves (validação básica)")
    void shouldPassWithPartialDiscProfile() {
        Quiz quiz = new Quiz(Map.of('D', 1.0, 'I', 0.5));
        Set<ConstraintViolation<Quiz>> violations = validator.validate(quiz);

        assertTrue(violations.isEmpty());
    }
}
