package com.mamadaliev.github.api.api.resource;

import com.mamadaliev.github.api.api.dto.UserDto;
import com.mamadaliev.github.api.service.GithubUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class GithubUserController {

    private final GithubUserService githubUserService;

    @Autowired
    public GithubUserController(GithubUserService githubUserService) {
        this.githubUserService = githubUserService;
    }

    @GetMapping("/users")
    public List<UserDto> retrieveUsers() {
        return githubUserService.retrieveAll("0", "100");
    }
}
