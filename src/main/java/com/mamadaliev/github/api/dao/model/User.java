package com.mamadaliev.github.api.dao.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Id;

@Data
@EqualsAndHashCode
@Builder
public class User {

    @Id
    private long id;

    private String login;

    private long followersCount;
}
