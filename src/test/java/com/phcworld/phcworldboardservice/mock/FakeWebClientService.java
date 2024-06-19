package com.phcworld.phcworldboardservice.mock;

import com.phcworld.phcworldboardservice.controller.port.FreeBoardSearch;
import com.phcworld.phcworldboardservice.controller.port.WebclientService;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.exception.model.NotFoundException;
import com.phcworld.phcworldboardservice.service.port.FreeBoardAnswerResponse;
import com.phcworld.phcworldboardservice.service.port.UserResponse;

import java.util.*;
import java.util.stream.Collectors;

public class FakeWebClientService implements WebclientService {

    private List<UserResponse> users = new ArrayList<>();

    private List<FreeBoardAnswerResponse> answers = new ArrayList<>();

    public FakeWebClientService() {
        UserResponse user1 = UserResponse.builder()
                .name("테스트")
                .profileImage("image")
                .userId("1111")
                .build();
        UserResponse user2 = UserResponse.builder()
                .name("테스트2")
                .profileImage("image")
                .userId("2222")
                .build();
        users.add(user1);
        users.add(user2);

        answers.add(FreeBoardAnswerResponse.builder()
                        .writer(user1)
                        .answerId("answer-id1")
                        .contents("답변1")
                        .boardId(1L)
                        .updatedDate("방금전")
                .build());
        answers.add(FreeBoardAnswerResponse.builder()
                .writer(user2)
                .answerId("answer-id2")
                .boardId(1L)
                .contents("답변2")
                .updatedDate("1시간전")
                .build());
    }

    @Override
    public List<FreeBoardAnswerResponse> getAnswers(String token, FreeBoard freeBoard) {
        return answers.stream().filter(answer -> answer.boardId().equals(freeBoard.getId())).toList();
    }

    @Override
    public UserResponse getUser(String token, FreeBoard freeBoard) {
        String userId = "";
        if(freeBoard == null){
            userId = token;
        } else {
            userId = freeBoard.getWriterId();
        }
        String finalUserId = userId;
        return users.stream()
                .filter(user -> user.userId().equals(finalUserId))
                .findAny()
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Map<String, UserResponse> getUsers(String token, List<FreeBoard> freeBoards) {
        List<String> userIds = freeBoards.stream()
                .map(FreeBoard::getWriterId)
                .distinct()
                .toList();
        Map<String, UserResponse> map = new HashMap<>();

        users.stream()
                .filter(user -> userIds.contains(user.userId()))
                .forEach(user -> map.put(user.userId(), user));
        return map;
    }

    @Override
    public FreeBoardSearch getUserIdByName(String token, FreeBoardSearch search) {
        List<String> userIds = users.stream()
                .filter(user -> user.name().equals(search.keyword()))
                .map(UserResponse::userId)
                .toList();
        return FreeBoardSearch.builder()
                .userIds(userIds)
                .pageNum(search.pageNum())
                .pageSize(search.pageSize())
                .keyword(search.keyword())
                .searchType(search.searchType())
                .build();
    }
}
