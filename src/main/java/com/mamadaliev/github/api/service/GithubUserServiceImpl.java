package com.mamadaliev.github.api.service;

import com.mamadaliev.github.api.api.dto.UserDto;
import com.mamadaliev.github.api.dao.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

import static com.mamadaliev.github.api.helper.EndpointHelper.GITHUB_AUTHORIZATION;
import static com.mamadaliev.github.api.helper.EndpointHelper.GITHUB_USERS;

@Slf4j
@Service
public class GithubUserServiceImpl implements GithubUserService {

    private final RestTemplate restTemplate;

    @Autowired
    public GithubUserServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<UserDto> retrieveAll(@DefaultValue("0") String since,
                                     @DefaultValue("100") String perPage) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("accept", "application/vnd.github.v3+json");
        parameters.put("since", since);
        parameters.put("per_page", perPage);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GITHUB_USERS);
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Authorization", GITHUB_AUTHORIZATION);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ResponseEntity<UserDto[]> users = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserDto[].class,
                Map.of("since", since),
                Map.of("per_page", perPage),
                Map.of("accept", "application/vnd.github.v3+json"));

        if (users.getStatusCode() != HttpStatus.OK || Objects.requireNonNull(users.getBody()).length == 0) {
            return null;
        }

        var userStore = new LinkedList<>(Arrays.asList(users.getBody()));
        log.info("{}", userStore);
        var lastUserId = String.valueOf(userStore.getLast().getId());
        var result = retrieveAll(lastUserId, perPage);
        var resultStore = new ArrayList<UserDto>();

        if (Objects.nonNull(result)) {
            resultStore.addAll(result);
        }

        return resultStore;
    }

    @Override
    public User retrieve(String login) {
        return null;
    }

    @Override
    public long retrieveFollowersCount(String login) {
        return 0;
    }
}
