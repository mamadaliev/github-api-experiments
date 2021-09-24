package com.mamadaliev.github.api.service.impl;

import com.mamadaliev.github.api.api.dto.UserDto;
import com.mamadaliev.github.api.dao.model.User;
import com.mamadaliev.github.api.dao.repository.UserRepository;
import com.mamadaliev.github.api.service.GithubUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static com.mamadaliev.github.api.helper.EndpointHelper.GITHUB_AUTHORIZATION;
import static com.mamadaliev.github.api.helper.EndpointHelper.GITHUB_USERS;

@Slf4j
@Service
@AllArgsConstructor
public class GithubUserServiceImpl implements GithubUserService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

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

        ResponseEntity<UserDto[]> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserDto[].class,
                Map.of("since", since),
                Map.of("per_page", perPage),
                Map.of("accept", "application/vnd.github.v3+json"));

        if (response.getStatusCode() != HttpStatus.OK || Objects.requireNonNull(response.getBody()).length == 0) {
            return null;
        }

        var userStore = new LinkedList<>(Arrays.asList(response.getBody()));

        log.info("{}", userStore);

        List<User> users = userStore.stream()
                .map(user -> modelMapper.map(user, User.class))
                .collect(Collectors.toList());

        userRepository.saveAll(users);

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

    @Override
    public void retrieveInThreads(long from, long to, long count) {
        retrieve(from, to, count);
    }

    private List<UserDto> retrieve(long from, long to, long count) {
        if (from + count - 1 >= to) {
            return null;
        }
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accept", "application/vnd.github.v3+json");
        parameters.put("since", from);
        parameters.put("per_page", count);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GITHUB_USERS);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Authorization", GITHUB_AUTHORIZATION);

        ResponseEntity<UserDto[]> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserDto[].class,
                Map.of("since", from),
                Map.of("per_page", to),
                Map.of("accept", "application/vnd.github.v3+json"));

        if (response.getStatusCode() != HttpStatus.OK || Objects.requireNonNull(response.getBody()).length == 0) {
            return null;
        }

        var userStore = new LinkedList<>(Arrays.asList(response.getBody()));

        log.info("{}", userStore);

        List<User> users = userStore.stream()
                .map(user -> modelMapper.map(user, User.class))
                .collect(Collectors.toList());

        userRepository.saveAll(users);

        var fromUserId = userStore.getLast().getId();
        return retrieve(fromUserId, to, count);
    }
}
