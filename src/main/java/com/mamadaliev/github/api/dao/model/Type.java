package com.mamadaliev.github.api.dao.model;

import lombok.Getter;

@Getter
public enum Type {
    USER("user");

    private final String name;

    Type(String name) {
        this.name = name;
    }
}
