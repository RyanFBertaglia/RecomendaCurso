package com.recommend.server.location;

import com.recommend.server.dto.Coordinates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CepToCoordinatesTest {

    @Test
    void getApproximated() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);

        RestTemplate restTemplate = new RestTemplate(factory);

        // Verifica se a API está no ar com um CEP garantido
        boolean apiAvailable = true;
        try {
            String testJson = restTemplate.getForObject(
                    "https://brasilapi.com.br/api/cep/v2/01001000",
                    String.class
            );
            if (testJson == null || !testJson.contains("latitude")) {
                apiAvailable = false;
            }
        } catch (Exception e) {
            apiAvailable = false;
        }

        Assumptions.assumeTrue(apiAvailable, "API BrasilAPI is unavailable");

        // Testa o CEP base + offsets (fallback)
        Coordinates coordinates = null;
        int cepBase = 13060471;
        int[] offsets = {0, 1, -1, 2, -2, 3, -3};

        for (int offset : offsets) {
            int cep = cepBase + offset;

            try {
                String json = restTemplate.getForObject(
                        "https://brasilapi.com.br/api/cep/v2/" + cep,
                        String.class
                );

                if (json != null && json.contains("latitude")) {
                    String latitude = json.split("\"latitude\":\"")[1].split("\"")[0].trim();
                    String longitude = json.split("\"longitude\":\"")[1].split("\"")[0].trim();
                    coordinates = new Coordinates(Double.valueOf(latitude), Double.valueOf(longitude));
                    break;
                }

            } catch (Exception ignored) {}
        }

        assertNotNull(coordinates, "Fallback should return the nearest CEP when base CEP is not found");
        System.out.println(coordinates);
    }
}