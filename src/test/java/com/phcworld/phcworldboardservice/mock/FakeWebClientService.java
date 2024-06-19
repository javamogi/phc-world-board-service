package com.phcworld.phcworldboardservice.mock;

import com.phcworld.phcworldboardservice.controller.port.WebclientService;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.exception.model.NotFoundException;
import com.phcworld.phcworldboardservice.service.port.FreeBoardAnswerResponse;
import com.phcworld.phcworldboardservice.service.port.UserResponse;

import java.util.*;

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

        for (int i = 0; i < userIds.size(); i++){
            int finalI = i;
            Optional<UserResponse> user = users.stream()
                    .filter(u -> u.userId().equals(userIds.get(finalI)))
                    .findAny();
            user.ifPresent(userResponse -> map.put(userIds.get(finalI), userResponse));
        }

        return map;
    }
}
