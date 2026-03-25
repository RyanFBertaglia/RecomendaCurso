package com.recommend.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommend.server.dto.*;
import com.recommend.server.model.CourseImp;
import com.recommend.server.service.FafylService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootTest
public class GeneralTest {

    @TestConfiguration
    static class Config {

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Autowired
    private FafylService fafylService;

    @Test
    void shouldReturnRankedCoursesBasedOnAbilities() {
        Quiz quiz = new Quiz(
                List.of("Cálculo", "Memorização"),
                List.of()
        );

        List<Fafyl> result = fafylService.findAllFafyl(quiz);
        IO.println(result);
    }

    @Test
    void shouldReturnInDistance() {
        CoursesRequest coursesRequest = new CoursesRequest(
                List.of(1, 2, 3),
                100000.0,
                new Coordinates(-22.9059, -47.0590)
        );
        List<CourseImpDTO> courses = fafylService.findInDistance(coursesRequest.courses(), coursesRequest.distance(), coursesRequest.location());
        IO.println(courses);
    }
}
