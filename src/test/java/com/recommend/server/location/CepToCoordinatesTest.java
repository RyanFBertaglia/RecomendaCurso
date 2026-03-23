package com.recommend.server.location;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommend.server.dto.Coordinates;
import com.recommend.server.exception.LocationNotFound;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CepToCoordinatesTest {

    @Test
    void getApproximated() {
        // As a difference of 1/2 streets doesn't make any difference
        // change a little the cep is not so bad
        RestTemplate restTemplate = new RestTemplate();
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
        assertNotNull(coordinates, "It should give an approximated location");
        System.out.println(coordinates);
    }
}
