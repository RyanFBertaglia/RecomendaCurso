package com.recommend.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class Question {
    private Integer id;
    private String text;
    private List<Alternative> alternatives;

    public Alternative getAlternative(Integer id) {
        return alternatives.stream()
                .filter(alternative -> alternative.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
