package com.phcworld.phcworldboardservice.service;

import com.phcworld.phcworldboardservice.controller.port.WebclientService;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.service.port.FreeBoardAnswerResponse;
import com.phcworld.phcworldboardservice.service.port.UserResponse;
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
public class WebclientServiceImpl implements WebclientService {

    private final WebClient.Builder webClient;
    private final Environment env;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Override
    public List<FreeBoardAnswerResponse> getAnswers(String token, FreeBoard freeBoard) {

        log.info("Before call users microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        List<FreeBoardAnswerResponse> answers = circuitBreaker.run(
                () -> webClient.build()
//                        .mutate().baseUrl("http://localhost:8080/answers")
				        .mutate().baseUrl(env.getProperty("answer_service.url"))
                        .build()
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/freeboards/{id}")
                                .build(freeBoard.getId()))
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<FreeBoardAnswerResponse>>() {})
                        .block(),
                throwable -> new ArrayList<>());
        log.info("After called users microservice");

        return answers;
    }

}
