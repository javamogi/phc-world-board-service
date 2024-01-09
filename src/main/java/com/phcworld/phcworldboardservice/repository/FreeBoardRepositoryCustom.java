package com.phcworld.phcworldboardservice.repository;

import com.phcworld.phcworldboardservice.dto.FreeBoardSearchDto;
import com.phcworld.phcworldboardservice.dto.FreeBoardSelectDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FreeBoardRepositoryCustom {
    List<FreeBoardSelectDto> findByKeyword(FreeBoardSearchDto searchDto, Pageable pageable);
}
