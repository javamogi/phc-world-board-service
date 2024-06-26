package com.phcworld.phcworldboardservice.controller.port;

import com.phcworld.phcworldboardservice.infrastructure.dto.FreeBoardSearch;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.domain.FreeBoardRequest;

import java.util.List;

public interface FreeBoardService {
    FreeBoard register(FreeBoardRequest request);
    List<FreeBoard> getSearchList(FreeBoardSearch search);
    FreeBoard update(FreeBoardRequest request);
    FreeBoard delete(Long boardId);
    List<FreeBoard> getFreeBoardsByUserId(String userId);

    FreeBoard existBoard(Long boardId);

    FreeBoard getFreeBoard(Long boardId);
}
