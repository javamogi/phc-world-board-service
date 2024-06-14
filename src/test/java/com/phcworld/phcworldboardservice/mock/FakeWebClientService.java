package com.phcworld.phcworldboardservice.mock;

import com.phcworld.phcworldboardservice.controller.port.WebclientService;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.service.port.FreeBoardAnswerResponse;
import com.phcworld.phcworldboardservice.service.port.UserResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeWebClientService implements WebclientService {
    @Override
    public UserResponse getUser(String token, FreeBoard freeBoard) {
        return UserResponse.builder()
                .userId(freeBoard.getWriterId())
                .profileImage("blank.jpg")
                .email("test0@test.test")
                .name("테스트0")
                .build();
    }

    @Override
    public Map<String, UserResponse> getUsers(String token, List<FreeBoard> freeBoards) {
        List<String> userIds = freeBoards.stream()
                .map(FreeBoard::getWriterId)
                .distinct()
                .toList();
        Map<String, UserResponse> map = new HashMap<>();
        for (int i = 0; i < userIds.size(); i++) {
            String userId = userIds.get(i);
            UserResponse user = UserResponse.builder()
                    .userId(userId)
                    .profileImage("blank.jpg")
                    .email("test" + i + "@test.test")
                    .name("테스트" + i)
                    .build();
            map.put(userId, user);
        }
        return map;
    }

    @Override
    public List<FreeBoardAnswerResponse> getAnswers(String token, FreeBoard freeBoard) {
        return new ArrayList<>();
    }
}
