package com.recommend.server.ia;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

public class AnswerQuestionsTest {
    private static OrtEnvironment env;
    private static OrtSession session;

    @BeforeAll
    public static void setup() {
        env = OrtEnvironment.getEnvironment();
    }

    @AfterAll
    public static void cleanup() throws OrtException {
        if (session != null) session.close();
    }

    @BeforeEach
    void loadModel() {
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        try {
            session = env.createSession("src/test/resources/chatbot.onnx", options);
        } catch (OrtException e) { throw new RuntimeException("Error while loading the model"); }
    }

    @Test
    public void testAnswerQuestions() throws OrtException {
        String question = "Como me é recomendado um curso?";
        try {
            OnnxTensor inputTensor = OnnxTensor.createTensor(
                    env,
                    new String[][]{{question}}
            );

            OrtSession.Result result = session.run(
                    Collections.singletonMap(
                            session.getInputNames().iterator().next(),
                            inputTensor
                    )
            );

            Object output = result.get(0).getValue();
            String label = ((String[]) output)[0];

            IO.println(mapResponse(label));

        } catch (Exception e) {
            IO.println("Error understanding the question");
        }
    }

    private String mapResponse(String label) {
        return switch (label) {
            case "fafyl" -> "O FAFYL recomenda cursos com base nas suas habilidades.";
            case "quiz" -> "Você responde um quiz com suas habilidades.";
            case "abilities" -> "Cada curso tem um perfil DISC ideal (pesos D, I, S, C).";
            case "cantbe" -> "O filtro por incompatibilidade foi removido; o ranking é puramente DISC.";
            case "recomendacao" -> "O sistema calcula o produto escalar entre seu perfil DISC e os pesos do curso.";
            case "score" -> "Score é o resultado do produto escotal DISC entre seu perfil e o curso.";
            case "sem_resultado" -> "Nenhum curso combinou com seu perfil.";
            case "ranking" -> "Cursos com mais match aparecem primeiro.";
            case "melhorar" -> "Adicione mais habilidades no quiz.";
            case "historico" -> "O histórico guarda suas recomendações.";
            case "capelinho" -> "Capelinho é o mascote visual.";

            case "login" -> "Verifique email e senha.";
            case "senha" -> "Use recuperação de senha.";
            case "token" -> "Sessão expirada, faça login novamente.";
            case "erro" -> "Tente novamente ou verifique conexão.";

            case "curso_info" -> "Veja detalhes do curso na plataforma.";
            case "habilidades" -> "Cada curso exige habilidades específicas.";
            case "match" -> "O sistema calcula compatibilidade.";
            case "local" -> "Veja faculdades disponíveis na tela.";
            case "preco" -> "O valor varia por faculdade.";

            case "criar_conta" -> "Use a tela de cadastro.";
            case "localizacao" -> "Atualize no perfil.";

            case "tempo_quiz" -> "O quiz é rápido e leva poucos minutos.";
            case "refazer_quiz" -> "Você pode refazer o quiz quando quiser.";
            case "dados_privacidade" -> "Seus dados são usados apenas para recomendações.";
            case "suporte" -> "Se precisar, entre em contato com o suporte.";
            case "bug" -> "Se encontrou um erro, tente atualizar ou avisar o suporte.";
            case "plataforma" -> "Você pode acessar o FAFYL pelo navegador.";
            case "compatibilidade" -> "A compatibilidade depende das suas habilidades e interesses.";
            case "inicio" -> "Comece criando uma conta e respondendo o quiz.";
            case "perfil" -> "Você pode editar suas informações no perfil.";
            case "requisitos" -> "Cada curso possui requisitos específicos de habilidades.";

            default -> "Não entendi sua pergunta.";
        };
    }
}
