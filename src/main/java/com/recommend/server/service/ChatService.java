package com.recommend.server.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;

@Service
public class ChatService {

    private static OrtEnvironment env;
    private static OrtSession session;


    public ChatService() {
        env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        try {
            InputStream modelStream = getClass().getResourceAsStream("/chatbot.onnx");
            File tempModel = File.createTempFile("chatbot", ".onnx");
            tempModel.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(tempModel)) {
                modelStream.transferTo(out);
            }
            session = env.createSession(tempModel.getAbsolutePath(), options);
        } catch (Exception e) {
            throw new RuntimeException("Error while loading the model");
        }
    }

    public String respond(String question) {
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

            return mapResponse(label);

        } catch (Exception e) {
            throw new RuntimeException("Error understanding the question");
        }
    }

    private String mapResponse(String label) {
        return switch (label) {
            case "fafyl" -> "O FAFYL recomenda cursos com base nas suas habilidades.";
            case "quiz" -> "Você responde um quiz com suas habilidades.";
            case "abilities" -> "Abilities são características que combinam com cursos.";
            case "cantbe" -> "CantBe elimina cursos incompatíveis.";
            case "recomendacao" -> "O sistema compara suas habilidades com os cursos.";
            case "score" -> "Score é o número de habilidades em comum.";
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
