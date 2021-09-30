package com.mamadaliev.github.api.service;

import com.mamadaliev.github.api.dao.model.User;

public interface GithubUserService {

    User retrieve(String login);

    long retrieveFollowersCount(String login);

    void retrieveAll(long from, long to, long count);
}
