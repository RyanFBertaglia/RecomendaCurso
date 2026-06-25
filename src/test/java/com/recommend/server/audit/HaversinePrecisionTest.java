package com.recommend.server.audit;

import com.recommend.server.dto.Coordinates;
import com.recommend.server.service.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class HaversinePrecisionTest {

    @Autowired
    private Location location;

    @Test
    @DisplayName("BUG HIGH-1: Haversine with identical coordinates causes acos(1.0) which is fine, but near-identical can cause acos(>1)")
    void haversineWithIdenticalPointsShouldReturnZero() {
        Coordinates a = new Coordinates(-22.9059, -47.0590);
        Coordinates b = new Coordinates(-22.9059, -47.0590);
        double distance = location.haversine(a, b);
        assertEquals(0.0, distance, 0.001);
    }

    @Test
    @DisplayName("BUG HIGH-1: Haversine with very close points can trigger acos domain error due to float precision")
    void haversineWithVeryClosePointsShouldNotExplode() {
        Coordinates a = new Coordinates(-22.9059, -47.0590);
        Coordinates b = new Coordinates(-22.9059001, -47.0590001);
        double distance = location.haversine(a, b);
        assertTrue(distance >= 0, "Distance should be non-negative, got: " + distance);
    }

    @Test
    @DisplayName("BUG HIGH-1: Extreme latitude values should not cause acos domain error")
    void haversineWithExtremeLatitudesShouldNotExplode() {
        Coordinates northPole = new Coordinates(90.0, 0.0);
        Coordinates southPole = new Coordinates(-90.0, 0.0);
        double distance = location.haversine(northPole, southPole);
        assertTrue(distance > 0);
        assertTrue(Double.isFinite(distance));
    }
}
