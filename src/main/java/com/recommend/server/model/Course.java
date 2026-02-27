package com.recommend.server.model;

import java.util.List;

public record Course(String nome, List<String> hab) {

    public int compare(List<String> habUsuario) {
        return (int) habUsuario.stream()
                .filter(this.hab::contains)
                .count();
    }
}