package com.phcworld.phcworldboardservice.infrastructure;

import com.phcworld.phcworldboardservice.infrastructure.dto.FreeBoardSearch;
import com.phcworld.phcworldboardservice.infrastructure.dto.FreeBoardSelectDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FreeBoardJpaRepositoryCustom {
    List<FreeBoardSelectDto> findByKeyword(FreeBoardSearch searchDto, Pageable pageable);
}
