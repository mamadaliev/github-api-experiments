package com.mamadaliev.github.api.api.resource;

import com.mamadaliev.github.api.api.dto.UserDto;
import com.mamadaliev.github.api.service.GithubUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public List<UserDto> retrieveUsers(@RequestParam String since) {
        return githubUserService.retrieveAll(since, "100");
    }

    @GetMapping("/usersInThreads")
    public String retrieveUsersInThreads(@RequestParam long from, @RequestParam long to) {
        long count = 100;
        githubUserService.retrieveInThreads(from, to, count);
        return "Ok";
    }
}
