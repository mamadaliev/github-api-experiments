package com.mamadaliev.github.api.service.impl;

import com.mamadaliev.github.api.api.dto.UserDto;
import com.mamadaliev.github.api.dao.model.User;
import com.mamadaliev.github.api.dao.repository.UserRepository;
import com.mamadaliev.github.api.service.GithubUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.mamadaliev.github.api.helper.EndpointHelper.*;

@Slf4j
@Service
@AllArgsConstructor
public class GithubUserServiceImpl implements GithubUserService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public User retrieve(String login) {
        return null;
    }

    @Override
    public long retrieveFollowersCount(String login) {
        return 0;
    }

    // 5000 requests per 1 hour
    @Override
    public void retrieveAll(long from, long to, long count) {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        int number = 300_000;

        long x1 = from + number;
        long x2 = from + number + number;
        long x3 = from + number + number + number;
        long x4 = from + number + number + number + number;
        long x5 = from + number + number + number + number + number;

        executor.submit(() -> retrieve(from, x1, count, TOKENS.get(0)));
        executor.submit(() -> retrieve(x1, x2, count, TOKENS.get(1)));
        executor.submit(() -> retrieve(x2, x3, count, TOKENS.get(2)));
        executor.submit(() -> retrieve(x3, x4, count, TOKENS.get(3)));
        executor.submit(() -> retrieve(x4, x5, count, TOKENS.get(4)));
    }

    private void retrieve(long from, long to, long count, String token) {
        if (from + count - 1 >= to) {
            return;
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
        headers.set("Authorization", token);

        ResponseEntity<UserDto[]> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserDto[].class,
                Map.of("since", from),
                Map.of("per_page", to),
                Map.of("accept", "application/vnd.github.v3+json"));

        if (response.getStatusCode() != HttpStatus.OK || Objects.requireNonNull(response.getBody()).length == 0) {
            return;
        }

        var userStore = new LinkedList<>(Arrays.asList(response.getBody()));

        userRepository.saveAll(userStore.stream()
                .map(user -> modelMapper.map(user, User.class))
//                .peek(user -> log.info("{}: {}", user.getId(), user.getLogin()))
//                .filter(user -> !userRepository.existsByLogin(user.getLogin()))
                .collect(Collectors.toList()));
        var fromUserId = userStore.getLast().getId();
        log.info("thread={}, userId={}", Thread.currentThread().getName(), fromUserId);
        retrieve(fromUserId, to, count, token);
    }
}
