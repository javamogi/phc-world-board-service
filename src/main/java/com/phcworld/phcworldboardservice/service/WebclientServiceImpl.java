package com.phcworld.phcworldboardservice.service;

import com.phcworld.phcworldboardservice.exception.model.ErrorCode;
import com.phcworld.phcworldboardservice.infrastructure.dto.FreeBoardSearch;
import com.phcworld.phcworldboardservice.controller.port.WebclientService;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.exception.model.NotFoundException;
import com.phcworld.phcworldboardservice.security.utils.SecurityUtil;
import com.phcworld.phcworldboardservice.service.dto.FreeBoardAnswerResponse;
import com.phcworld.phcworldboardservice.service.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebclientServiceImpl implements WebclientService {

    private final WebClient.Builder webClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Value("${user_service.url}")
    private String userUrl;

    @Value("${answer_service.url}")
    private String answerUrl;

    @Override
    public UserResponse getUser(String token, FreeBoard board){
        String userId = "";
        if (board == null){
            userId = SecurityUtil.getCurrentMemberId();
        } else {
            userId = board.getWriterId();
        }
        String finalUserId = userId;
        return webClient.build()
                .mutate().baseUrl(userUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{userId}")
                        .build(finalUserId))
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.just(new NotFoundException(ErrorCode.USER_NOT_FOUND)))
                .bodyToMono(UserResponse.class)
                .block();
    }

    @Override
    public Map<String, UserResponse> getUsers(String token, List<FreeBoard> freeBoards) {

        List<String> userIds = freeBoards.stream()
                .map(FreeBoard::getWriterId)
                .distinct()
                .toList();

        log.info("Before call users microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        Map<String, UserResponse> users = circuitBreaker.run(
                () -> webClient.build()
                        .mutate().baseUrl(userUrl)
                        .build()
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("")
                                .queryParam("userIds", userIds)
                                .build())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, UserResponse>>() {})
                        .block(),
                throwable -> new HashMap<>());
        log.info("After called users microservice");
        return users;
    }

    @Override
    public FreeBoardSearch getUserIdByName(String token, FreeBoardSearch search) {
        log.info("Before call users microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        List<UserResponse> users = circuitBreaker.run(
                () -> webClient.build()
                .mutate().baseUrl(userUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("name", search.keyword())
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserResponse>>() {})
                .block(),
                throwable -> new ArrayList<>());
        log.info("After called users microservice");
        List<String> ids = users.stream()
                .map(UserResponse::userId)
                .toList();
        return FreeBoardSearch.builder()
                .userIds(ids)
                .pageNum(search.pageNum())
                .pageSize(search.pageSize())
                .keyword(search.keyword())
                .searchType(search.searchType())
                .build();
    }

    @Override
    public List<FreeBoardAnswerResponse> getAnswers(String token, FreeBoard freeBoard) {

        log.info("Before call users microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        List<FreeBoardAnswerResponse> answers = circuitBreaker.run(
                () -> webClient.build()
				        .mutate().baseUrl(answerUrl)
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
