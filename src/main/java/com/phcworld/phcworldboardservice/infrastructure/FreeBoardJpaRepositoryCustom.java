package com.phcworld.phcworldboardservice.infrastructure;

import com.phcworld.phcworldboardservice.controller.port.FreeBoardSearchDto;
import com.phcworld.phcworldboardservice.infrastructure.port.FreeBoardSelectDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FreeBoardJpaRepositoryCustom {
    List<FreeBoardSelectDto> findByKeyword(FreeBoardSearchDto searchDto, Pageable pageable);
}
