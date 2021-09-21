package com.mamadaliev.github.api.service;

import com.mamadaliev.github.api.model.User;

public interface GithubService {

    void retrieveAllUsersAndSave();

    User retrieveUser(String nickname);
}
