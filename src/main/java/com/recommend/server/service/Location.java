package com.recommend.server.service;

import com.recommend.server.dto.Coordinates;
import org.springframework.stereotype.Service;

@Service
public class Location {

    public double haversine(Coordinates from, Coordinates to) {
        final double R = 6371000;
        double dLat = Math.toRadians(to.lat() - from.lat());
        double dLon = Math.toRadians(to.lon() - from.lon());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(from.lat())) * Math.cos(Math.toRadians(to.lat())) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
