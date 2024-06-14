package com.phcworld.phcworldboardservice.controller.port;

import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.domain.port.FreeBoardRequest;

import java.util.List;

public interface FreeBoardService {
    FreeBoard register(FreeBoardRequest request);
    List<FreeBoard> getSearchList(FreeBoardSearch search);
    FreeBoard getFreeBoard(Long boardId);
    FreeBoard update(FreeBoardRequest request);
    FreeBoard delete(Long boardId);
    List<FreeBoard> getFreeBoardsByUserId(String userId);
}