package com.phcworld.phcworldboardservice.infrastructure;

import com.phcworld.phcworldboardservice.infrastructure.dto.FreeBoardSearch;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.infrastructure.dto.FreeBoardSelectDto;
import com.phcworld.phcworldboardservice.service.port.FreeBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jpaBoardRepository")
@RequiredArgsConstructor
public class FreeBoardRepositoryImpl implements FreeBoardRepository {

    private final FreeBoardJpaRepository freeBoardJpaRepository;

    @Override
    public List<FreeBoard> findByWriterId(String writerId) {
        return freeBoardJpaRepository.findByWriterId(writerId)
                .stream()
                .map(FreeBoardEntity::toModel)
                .toList();
    }

    @Override
    public List<FreeBoard> findByKeyword(FreeBoardSearch searchDto) {
        PageRequest pageRequest = PageRequest.of(
                searchDto.pageNum() - 1,
                searchDto.pageSize(),
                Sort.by("createDate").descending());
        return freeBoardJpaRepository.findByKeyword(searchDto, pageRequest)
                .stream()
                .map(FreeBoardSelectDto::toModel)
                .toList();
    }

    @Override
    public FreeBoard save(FreeBoard freeBoard) {
        return freeBoardJpaRepository.save(FreeBoardEntity.from(freeBoard)).toModel();
    }

    @Override
    public Optional<FreeBoard> findByBoardId(String boardId) {
        return freeBoardJpaRepository.findByBoardId(boardId)
                .map(FreeBoardEntity::toModel);
    }

    // use answer consumer
    @Override
    public Optional<FreeBoard> findById(Long boardId) {
        return freeBoardJpaRepository.findById(boardId)
                .map(FreeBoardEntity::toModel);
    }
}
