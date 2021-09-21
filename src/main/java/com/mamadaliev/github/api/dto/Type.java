package com.mamadaliev.github.api.dto;

import lombok.Getter;

@Getter
public enum Type {
    USER("user");

    private final String name;

    Type(String name) {
        this.name = name;
    }
}
