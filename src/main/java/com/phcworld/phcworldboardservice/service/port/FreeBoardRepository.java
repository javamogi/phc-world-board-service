package com.phcworld.phcworldboardservice.service.port;

import com.phcworld.phcworldboardservice.controller.port.FreeBoardSearch;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FreeBoardRepository {
    List<FreeBoard> findByWriterId(String writerId);
    Optional<FreeBoard> findById(Long boardId);
    List<FreeBoard> findByKeyword(FreeBoardSearch searchDto, Pageable pageable);
    FreeBoard save(FreeBoard freeBoard);
}
