package com.recommend.server.fafyl;

import com.recommend.server.model.Alternative;
import com.recommend.server.model.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuizTest {

    private Question quiz;

    private final CourseDISC direito      = new CourseDISC("Direito",      1.0, 0.5, 0.8, 0.2);
    private final CourseDISC medicina     = new CourseDISC("Medicina",     0.8, 0.2, 1.0, 0.5);
    private final CourseDISC engenharia   = new CourseDISC("Engenharia",   0.5, 1.0, 0.2, 0.8);
    private final CourseDISC arquitetura  = new CourseDISC("Arquitetura",  0.2, 0.8, 0.5, 1.0);

    private final List<CourseDISC> courses = List.of(direito, medicina, engenharia, arquitetura);

    @BeforeEach
    void setUp() {
        Alternative altD = new Alternative(1, "Sim",  'D', 1.0);
        Alternative altI = new Alternative(2, "Não",  'I', 0.5);
        Alternative altS = new Alternative(3, "Sim",  'S', 0.8);
        Alternative altC = new Alternative(4, "Não",  'C', 0.2);
        quiz = new Question(1, "Você prefere liderar decisões?", List.of(altD, altI, altS, altC));
    }

    @Test @Order(1)
    @DisplayName("Alternative deve armazenar seus campos corretamente")
    void testAlternativeFields() {
        Alternative alt = new Alternative(1, "Sim", 'D', 1.0);
        assertAll("alternative fields",
                () -> assertEquals(1,    alt.getId()),
                () -> assertEquals("Sim", alt.getText()),
                () -> assertEquals('D',  alt.getDimension()),
                () -> assertEquals(1.0,  alt.getWeight(), 1e-9)
        );
    }

    @Test @Order(2)
    @DisplayName("Weight deve estar no intervalo [0.0, 1.0]")
    void testAlternativeWeightRange() {
        Alternative alt = new Alternative(1, "X", 'D', 0.75);
        assertTrue(alt.getWeight() >= 0.0 && alt.getWeight() <= 1.0,
                "Weight fora do range [0, 1]: " + alt.getWeight());
    }

    @Test @Order(3)
    @DisplayName("Question deve conter exatamente 4 alternativas")
    void testQuestionHasFourAlternatives() {
        assertEquals(4, quiz.getAlternatives().size());
    }

    @Test @Order(4)
    @DisplayName("getAlternative(1) deve retornar a alternativa D")
    void testGetAlternativeD() {
        Alternative alt = quiz.getAlternative(1);
        assertNotNull(alt);
        assertEquals('D', alt.getDimension());
        assertEquals(1.0, alt.getWeight(), 1e-9);
    }

    @ParameterizedTest(name = "getAlternative({0}) → dimensão {1}")
    @Order(5)
    @CsvSource({ "1,D", "2,I", "3,S", "4,C" })
    @DisplayName("Cada ID deve mapear para a dimensão correta")
    void testGetAlternativeByDimension(int id, char expected) {
        Alternative alt = quiz.getAlternative(id);
        assertNotNull(alt, "Alternativa não encontrada para id=" + id);
        assertEquals(expected, alt.getDimension());
    }

    @Test @Order(6)
    @DisplayName("getAlternative com ID inexistente deve retornar null ou lançar exceção")
    void testGetAlternativeInvalidId() {
        assertNull(quiz.getAlternative(99));
    }

    @Test @Order(7)
    @DisplayName("CourseDISC deve armazenar os scores corretamente")
    void testCourseDISCFields() {
        assertAll("Direito scores",
                () -> assertEquals("Direito", direito.getName()),
                () -> assertEquals(1.0, direito.getD(), 1e-9),
                () -> assertEquals(0.5, direito.getI(), 1e-9),
                () -> assertEquals(0.8, direito.getS(), 1e-9),
                () -> assertEquals(0.2, direito.getC(), 1e-9)
        );
    }

    @Test @Order(8)
    @DisplayName("Todos os scores de CourseDISC devem estar em [0.0, 1.0]")
    void testCourseScoresInRange() {
        for (CourseDISC course : courses) {
            assertAll(course.getName(),
                    () -> assertTrue(inRange(course.getD()), "D fora do range: " + course.getD()),
                    () -> assertTrue(inRange(course.getI()), "I fora do range: " + course.getI()),
                    () -> assertTrue(inRange(course.getS()), "S fora do range: " + course.getS()),
                    () -> assertTrue(inRange(course.getC()), "C fora do range: " + course.getC())
            );
        }
    }

    // -------------------------------------------------------
    // 4. Matching — curso mais compatível com respostas do usuário
    // -------------------------------------------------------

    @Test @Order(9)
    @DisplayName("Perfil alto em D deve recomendar Direito")
    void testMatchHighD() {
        // Usuário respondeu peso máximo em D, mínimo nas demais
        Map<Character, Double> userProfile = Map.of('D', 1.0, 'I', 0.0, 'S', 0.0, 'C', 0.0);
        CourseDISC best = findBestMatch(userProfile, courses);
        assertEquals("Direito", best.getName());
    }

    @Test @Order(10)
    @DisplayName("Perfil alto em I deve recomendar Engenharia (I=1.0)")
    void testMatchHighI() {
        Map<Character, Double> userProfile = Map.of('D', 0.0, 'I', 1.0, 'S', 0.0, 'C', 0.0);
        CourseDISC best = findBestMatch(userProfile, courses);
        assertEquals("Engenharia", best.getName());
    }

    @Test @Order(11)
    @DisplayName("Perfil alto em S deve recomendar Medicina (S=1.0)")
    void testMatchHighS() {
        Map<Character, Double> userProfile = Map.of('D', 0.0, 'I', 0.0, 'S', 1.0, 'C', 0.0);
        CourseDISC best = findBestMatch(userProfile, courses);
        assertEquals("Medicina", best.getName());
    }

    @Test @Order(12)
    @DisplayName("Perfil alto em C deve recomendar Arquitetura (C=1.0)")
    void testMatchHighC() {
        Map<Character, Double> userProfile = Map.of('D', 0.0, 'I', 0.0, 'S', 0.0, 'C', 1.0);
        CourseDISC best = findBestMatch(userProfile, courses);
        assertEquals("Arquitetura", best.getName());
    }

    @Test @Order(13)
    @DisplayName("Perfil balanceado não deve retornar null")
    void testMatchBalancedProfile() {
        Map<Character, Double> userProfile = Map.of('D', 0.5, 'I', 0.5, 'S', 0.5, 'C', 0.5);
        CourseDISC best = findBestMatch(userProfile, courses);
        assertNotNull(best);
    }

    private CourseDISC findBestMatch(Map<Character, Double> profile, List<CourseDISC> courses) {
        CourseDISC best = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (CourseDISC c : courses) {
            double score = profile.get('D') * c.getD()
                    + profile.get('I') * c.getI()
                    + profile.get('S') * c.getS()
                    + profile.get('C') * c.getC();
            if (score > bestScore) {
                bestScore = score;
                best = c;
            }
        }
        return best;
    }

    private boolean inRange(double v) {
        return v >= 0.0 && v <= 1.0;
    }

    @AllArgsConstructor
    @Getter
    static class CourseDISC {
        private String name;
        private double D;
        private double I;
        private double S;
        private double C;
    }
}