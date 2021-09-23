package com.mamadaliev.github.api.service;

import com.mamadaliev.github.api.api.dto.UserDto;
import com.mamadaliev.github.api.dao.model.User;

import java.util.List;

public interface GithubUserService {

    List<UserDto> retrieveAll(String since, String perPage);

    User retrieve(String login);

    long retrieveFollowersCount(String login);
}
