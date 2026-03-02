package com.recommend.server.model;

import lombok.Data;

import java.util.List;

@Data
public class Course {
    private int id;
    private String name;
    private List<String> abilities;
    private List<String> cantBe;
    private String description;

    public int compare(List<String> abilities) {
        return (int) abilities.stream()
                .filter(this.abilities::contains)
                .count();
    }
    public Course(String name, List<String> abilities) {
        this.name = name;
        this.abilities = abilities;
    }
}