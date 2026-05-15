package com.recommend.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Alternative {
    private Integer id;
    private String text;
    private Character dimension;
    private Double weight;
}
