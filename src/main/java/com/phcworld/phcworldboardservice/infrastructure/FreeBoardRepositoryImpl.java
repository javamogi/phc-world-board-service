package com.phcworld.phcworldboardservice.infrastructure;

import com.phcworld.phcworldboardservice.controller.port.FreeBoardSearchDto;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.infrastructure.port.FreeBoardSelectDto;
import com.phcworld.phcworldboardservice.service.port.FreeBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
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
    public Optional<FreeBoard> findById(Long boardId) {
        return freeBoardJpaRepository.findById(boardId)
                .map(FreeBoardEntity::toModel);
    }

    @Override
    public List<FreeBoard> findByKeyword(FreeBoardSearchDto searchDto, Pageable pageable) {
        return freeBoardJpaRepository.findByKeyword(searchDto, pageable)
                .stream()
                .map(FreeBoardSelectDto::toModel)
                .toList();
    }

    @Override
    public FreeBoard save(FreeBoard freeBoard) {
        return freeBoardJpaRepository.save(FreeBoardEntity.from(freeBoard)).toModel();
    }
}
