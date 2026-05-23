package com.recommend.server.fafyl;

import com.recommend.server.model.Course;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DiscScoringTest {

    private final Course direito     = buildCourse("Direito",     1.0, 0.5, 0.8, 0.2);
    private final Course medicina    = buildCourse("Medicina",    0.8, 0.2, 1.0, 0.5);
    private final Course engenharia  = buildCourse("Engenharia",  0.5, 1.0, 0.2, 0.8);
    private final Course arquitetura = buildCourse("Arquitetura", 0.2, 0.8, 0.5, 1.0);

    private final List<Course> courses = List.of(direito, medicina, engenharia, arquitetura);

    @Test @Order(1)
    @DisplayName("Perfil alto em D deve dar maior score para Direito")
    void testMatchHighD() {
        Map<Character, Double> profile = Map.of('D', 1.0, 'I', 0.0, 'S', 0.0, 'C', 0.0);
        Course best = findBestMatch(profile, courses);
        assertEquals("Direito", best.getName());
    }

    @Test @Order(2)
    @DisplayName("Perfil alto em I deve dar maior score para Engenharia")
    void testMatchHighI() {
        Map<Character, Double> profile = Map.of('D', 0.0, 'I', 1.0, 'S', 0.0, 'C', 0.0);
        Course best = findBestMatch(profile, courses);
        assertEquals("Engenharia", best.getName());
    }

    @Test @Order(3)
    @DisplayName("Perfil alto em S deve dar maior score para Medicina")
    void testMatchHighS() {
        Map<Character, Double> profile = Map.of('D', 0.0, 'I', 0.0, 'S', 1.0, 'C', 0.0);
        Course best = findBestMatch(profile, courses);
        assertEquals("Medicina", best.getName());
    }

    @Test @Order(4)
    @DisplayName("Perfil alto em C deve dar maior score para Arquitetura")
    void testMatchHighC() {
        Map<Character, Double> profile = Map.of('D', 0.0, 'I', 0.0, 'S', 0.0, 'C', 1.0);
        Course best = findBestMatch(profile, courses);
        assertEquals("Arquitetura", best.getName());
    }

    @Test @Order(5)
    @DisplayName("Perfil balanceado não deve retornar null")
    void testMatchBalancedProfile() {
        Map<Character, Double> profile = Map.of('D', 0.5, 'I', 0.5, 'S', 0.5, 'C', 0.5);
        Course best = findBestMatch(profile, courses);
        assertNotNull(best);
    }

    @Test @Order(6)
    @DisplayName("Scores devem estar em ordem descendente")
    void testScoresDescending() {
        Map<Character, Double> profile = Map.of('D', 1.0, 'I', 0.0, 'S', 0.0, 'C', 0.0);
        List<FafylWithScore> ranked = rankCourses(profile, courses);

        for (int i = 0; i < ranked.size() - 1; i++) {
            assertTrue(ranked.get(i).score() >= ranked.get(i + 1).score());
        }
    }

    @Test @Order(7)
    @DisplayName("Cursos com score zero devem ser filtrados")
    void testFilterZeroScore() {
        Map<Character, Double> profile = Map.of('D', 1.0, 'I', 0.0, 'S', 0.0, 'C', 0.0);
        List<Course> onlyZero = List.of(
                buildCourse("Zero", 0.0, 0.0, 0.0, 0.0)
        );
        List<FafylWithScore> ranked = rankCourses(profile, onlyZero);
        assertTrue(ranked.isEmpty());
    }

    @Test @Order(8)
    @DisplayName("dotProduct deve calcular corretamente")
    void testDotProductCalculation() {
        Course course = buildCourse("Test", 0.8, 0.3, 0.5, 0.9);
        Map<Character, Double> profile = Map.of('D', 1.0, 'I', 0.0, 'S', 0.5, 'C', 0.2);

        double score = course.dotProduct(profile);

        assertEquals(0.8 * 1.0 + 0.3 * 0.0 + 0.5 * 0.5 + 0.9 * 0.2, score, 1e-9);
    }

    private Course buildCourse(String name, double d, double i, double s, double c) {
        Course course = new Course();
        course.setName(name);
        course.setDiscWeights(Map.of('D', d, 'I', i, 'S', s, 'C', c));
        return course;
    }

    private Course findBestMatch(Map<Character, Double> profile, List<Course> courses) {
        return courses.stream()
                .map(c -> new FafylWithScore(c.dotProduct(profile), c))
                .filter(f -> f.score() > 0)
                .max((a, b) -> Double.compare(a.score(), b.score()))
                .map(f -> f.course())
                .orElse(null);
    }

    private List<FafylWithScore> rankCourses(Map<Character, Double> profile, List<Course> courses) {
        return courses.stream()
                .map(c -> new FafylWithScore(c.dotProduct(profile), c))
                .filter(f -> f.score() > 0)
                .sorted((a, b) -> Double.compare(b.score(), a.score()))
                .toList();
    }

    record FafylWithScore(double score, Course course) {}
}
