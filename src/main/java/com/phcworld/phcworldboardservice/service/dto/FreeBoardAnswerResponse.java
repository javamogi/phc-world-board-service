package com.phcworld.phcworldboardservice.service.dto;

import lombok.Builder;

@Builder
public record FreeBoardAnswerResponse(
        String answerId,
        UserResponse writer,
        Long boardId,
        String contents,
        String updatedDate
) {
}
