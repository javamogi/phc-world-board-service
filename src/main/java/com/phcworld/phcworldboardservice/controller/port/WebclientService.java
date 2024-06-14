package com.phcworld.phcworldboardservice.controller.port;

import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.service.port.FreeBoardAnswerResponse;
import com.phcworld.phcworldboardservice.service.port.UserResponse;

import java.util.List;
import java.util.Map;

public interface WebclientService {
    UserResponse getUser(String token, FreeBoard freeBoard);
    Map<String, UserResponse> getUsers(String token, List<FreeBoard> freeBoards);
    List<FreeBoardAnswerResponse> getAnswers(String token, FreeBoard freeBoard);
}
