package com.phcworld.phcworldboardservice.service.port;

import com.phcworld.phcworldboardservice.infrastructure.dto.FreeBoardSearch;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FreeBoardRepository {
    List<FreeBoard> findByWriterId(String writerId);
    List<FreeBoard> findByKeyword(FreeBoardSearch searchDto);
    FreeBoard save(FreeBoard freeBoard);
    Optional<FreeBoard> findByBoardId(String boardId);

    Optional<FreeBoard> findById(Long boardId);
}
