package com.phcworld.phcworldboardservice.controller.port;

import lombok.Builder;

@Builder
public record FreeBoardSearchDto(
        int pageNum,
        int pageSize,
        String keyword,
        // 0 : 제목, 1 : 내용, 3 : 글쓴이
        Integer searchType
) {
}
