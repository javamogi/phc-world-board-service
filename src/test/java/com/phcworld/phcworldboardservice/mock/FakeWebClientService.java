package com.phcworld.phcworldboardservice.mock;

import com.phcworld.phcworldboardservice.controller.port.WebclientService;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.service.port.FreeBoardAnswerResponse;

import java.util.ArrayList;
import java.util.List;

public class FakeWebClientService implements WebclientService {

    @Override
    public List<FreeBoardAnswerResponse> getAnswers(String token, FreeBoard freeBoard) {
        return new ArrayList<>();
    }
}
