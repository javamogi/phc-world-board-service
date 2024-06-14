package com.phcworld.phcworldboardservice.infrastructure;

import com.phcworld.phcworldboardservice.controller.port.FreeBoardSearch;
import com.phcworld.phcworldboardservice.infrastructure.port.FreeBoardSelectDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FreeBoardJpaRepositoryCustom {
    List<FreeBoardSelectDto> findByKeyword(FreeBoardSearch searchDto, Pageable pageable);
}
