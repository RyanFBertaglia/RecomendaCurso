package com.recommend.server.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class QuizValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldFailWhenAbilitiesIsEmpty() {
        Quiz quiz = new Quiz(Collections.emptyList(), List.of("skill1"));
        Set<ConstraintViolation<Quiz>> violations = validator.validate(quiz);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("abilities")));
    }

    @Test
    void shouldFailWhenAbilitiesIsNull() {
        Quiz quiz = new Quiz(null, List.of("skill1"));
        Set<ConstraintViolation<Quiz>> violations = validator.validate(quiz);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("abilities")));
    }

    @Test
    void shouldPassWhenAbilitiesIsNotEmpty() {
        Quiz quiz = new Quiz(List.of("skill1", "skill2"), List.of("cant1"));
        Set<ConstraintViolation<Quiz>> violations = validator.validate(quiz);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldPassWhenUserCantBeIsNull() {
        Quiz quiz = new Quiz(List.of("skill1"), null);
        Set<ConstraintViolation<Quiz>> violations = validator.validate(quiz);

        assertTrue(violations.isEmpty());
    }
}