package com.recommend.server.dto;

import java.util.List;

public record QuestionDTO(String text, List<AlternativeDTO> alternatives) {
}
