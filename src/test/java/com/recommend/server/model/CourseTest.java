package com.recommend.server.model;

import org.junit.jupiter.api.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CourseTest {

    @Test @Order(1)
    @DisplayName("Course deve armazenar discWeights corretamente")
    void testDiscWeightsStored() {
        Course course = new Course();
        course.setName("Direito");
        course.setDiscWeights(Map.of('D', 1.0, 'I', 0.5, 'S', 0.8, 'C', 0.2));
        course.setDescription("Estudo e aplicação das leis.");

        assertEquals("Direito", course.getName());
        assertEquals(4, course.getDiscWeights().size());
        assertEquals(1.0, course.getDiscWeights().get('D'), 1e-9);
        assertEquals(0.5, course.getDiscWeights().get('I'), 1e-9);
        assertEquals(0.8, course.getDiscWeights().get('S'), 1e-9);
        assertEquals(0.2, course.getDiscWeights().get('C'), 1e-9);
        assertEquals("Estudo e aplicação das leis.", course.getDescription());
    }

    @Test @Order(2)
    @DisplayName("Course deve permitir discWeights nulo")
    void testNullDiscWeights() {
        Course course = new Course();
        course.setName("Curso sem pesos");
        assertNull(course.getDiscWeights());
    }

    @Test @Order(3)
    @DisplayName("Course deve permitir discWeights vazio")
    void testEmptyDiscWeights() {
        Course course = new Course();
        course.setDiscWeights(Map.of());
        assertTrue(course.getDiscWeights().isEmpty());
    }

    @Test @Order(4)
    @DisplayName("dotProduct deve calcular corretamente com perfil completo")
    void testDotProduct() {
        Course course = new Course();
        course.setDiscWeights(Map.of('D', 0.8, 'I', 0.3, 'S', 0.5, 'C', 0.9));

        Map<Character, Double> profile = Map.of('D', 1.0, 'I', 0.0, 'S', 0.5, 'C', 0.2);

        double score = course.dotProduct(profile);

        // 0.8*1.0 + 0.3*0.0 + 0.5*0.5 + 0.9*0.2 = 0.8 + 0 + 0.25 + 0.18 = 1.23
        assertEquals(1.23, score, 1e-9);
    }

    @Test @Order(5)
    @DisplayName("dotProduct com discWeights nulo deve retornar 0")
    void testDotProductNullWeights() {
        Course course = new Course();
        course.setDiscWeights(null);

        Map<Character, Double> profile = Map.of('D', 1.0, 'I', 0.5, 'S', 0.8, 'C', 0.2);

        assertEquals(0.0, course.dotProduct(profile), 1e-9);
    }

    @Test @Order(6)
    @DisplayName("dotProduct com perfil faltando letra deve usar 0 para aquela letra")
    void testDotProductMissingProfileKey() {
        Course course = new Course();
        course.setDiscWeights(Map.of('D', 1.0, 'I', 0.5, 'S', 0.8, 'C', 0.2));

        // Perfil sem 'C'
        Map<Character, Double> profile = Map.of('D', 1.0, 'I', 0.5, 'S', 0.8);

        // 1.0*1.0 + 0.5*0.5 + 0.8*0.8 + 0.2*0 = 1.0 + 0.25 + 0.64 = 1.89
        assertEquals(1.89, course.dotProduct(profile), 1e-9);
    }

    @Test @Order(7)
    @DisplayName("Construtor com nome e discWeights deve funcionar")
    void testConstructorWithNameAndWeights() {
        Course course = new Course("Engenharia", Map.of('D', 0.5, 'I', 1.0, 'S', 0.2, 'C', 0.8));

        assertEquals("Engenharia", course.getName());
        assertEquals(0.5, course.getDiscWeights().get('D'), 1e-9);
        assertEquals(1.0, course.getDiscWeights().get('I'), 1e-9);
    }
}
