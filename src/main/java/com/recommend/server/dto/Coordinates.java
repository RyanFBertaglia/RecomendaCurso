package com.recommend.server.dto;

import jakarta.persistence.Embeddable;

@Embeddable
public record Coordinates(Double lat, Double lon) {
}
