package com.phcworld.phcworldboardservice.service;

import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.dto.FreeBoardAnswerResponseDto;
import com.phcworld.phcworldboardservice.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebclientService {

    private final WebClient.Builder webClient;
    private final Environment env;
    private final CircuitBreakerFactory circuitBreakerFactory;

    public UserResponseDto getUserResponseDto(String token, FreeBoard freeBoard) {

        log.info("Before call users microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");

        UserResponseDto user = circuitBreaker.run(
                () -> webClient.build()
//                        .mutate().baseUrl("http://localhost:8080/users")
				        .mutate().baseUrl(env.getProperty("user_service.url"))
                        .build()
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/{id}")
                                .build(freeBoard.getWriterId()))
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .retrieve()
                        .bodyToMono(UserResponseDto.class)
                        .block(),
                throwable -> UserResponseDto.builder()
                        .email("")
                        .name("")
                        .createDate("")
                        .profileImage("")
                        .userId("")
                        .build());
        log.info("After called users microservice");
        return user;
    }

    public Map<String, UserResponseDto> getUserResponseDtoMap(String token, List<String> userIds) {

        log.info("Before call users microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        Map<String, UserResponseDto> users = circuitBreaker.run(
                () -> webClient.build()
//                        .mutate().baseUrl("http://localhost:8080/users")
				        .mutate().baseUrl(env.getProperty("user_service.url"))
                        .build()
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("")
                                .queryParam("userIds", userIds)
                                .build())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, UserResponseDto>>() {})
                        .block(),
                throwable -> new HashMap<>());
        log.info("After called users microservice");
        return users;
    }

    public List<FreeBoardAnswerResponseDto> getFreeBoardAnswerResponseDtoList(String token, FreeBoard freeBoard) {

        log.info("Before call users microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        List<FreeBoardAnswerResponseDto> answers = circuitBreaker.run(
                () -> webClient.build()
                        .mutate().baseUrl("http://localhost:8080/answers")
//				        .mutate().baseUrl(env.getProperty("answer_service.url"))
                        .build()
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/freeboards/{id}")
                                .build(freeBoard.getBoardId()))
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<FreeBoardAnswerResponseDto>>() {})
                        .block(),
                throwable -> new ArrayList<>());
        log.info("After called users microservice");

        return answers;
    }

}
