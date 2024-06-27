package com.phcworld.phcworldboardservice.controller.port;

import com.phcworld.phcworldboardservice.infrastructure.dto.FreeBoardSearch;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.service.dto.FreeBoardAnswerResponse;
import com.phcworld.phcworldboardservice.service.dto.UserResponse;

import java.util.List;
import java.util.Map;

public interface WebclientService {
    List<FreeBoardAnswerResponse> getAnswers(String token, FreeBoard freeBoard);
    UserResponse getUser(String token, FreeBoard freeBoard);

    Map<String, UserResponse> getUsers(String token, List<FreeBoard> freeBoards);

    FreeBoardSearch getUserIdByName(String token, FreeBoardSearch search);
}
