package com.recommend.server.location;

import com.recommend.server.dto.Coordinates;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DistancePointsTest {

    @Value("${geoapify.api.key}")
    private String API_KEY;

    RestTemplate restTemplate;

    @Test
    void getDistance() {
        Coordinates firstAddress = new Coordinates(-23.55052, -46.633308);
        Coordinates secondAddress = new Coordinates(-23.55052, -46.633308);

        String url = "https://api.geoapify.com/v1/routing"
                + "?waypoints=" + firstAddress.lat() + "," + firstAddress.lon() + "|"
                + secondAddress.lat() + "," + secondAddress.lon()
                + "&mode=drive"
                + "&apiKey=" + API_KEY;

        String json = new RestTemplate().getForObject(url, String.class);

        Pattern pattern = Pattern.compile("\"distance\":\\s*(\\d+(?:\\.\\d+)?)\\s*,\\s*\"time\":\\s*(\\d+(?:\\.\\d+)?)");
        assertNotNull(json, "The API must return something");

        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            double distance = Double.parseDouble(matcher.group(1));
            double time = Double.parseDouble(matcher.group(2));

            assertTrue(distance <= 0, "Fail with distance");
            assertTrue(time <= 0, "Fail with time");

            IO.println("Distance (m): " + distance);
            IO.println("Time (s): " + time);
        } else {
            IO.println("Não foi possível extrair distance e time");
        }
    }
}
