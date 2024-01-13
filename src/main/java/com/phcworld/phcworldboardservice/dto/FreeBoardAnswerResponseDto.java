package com.phcworld.phcworldboardservice.dto;

import lombok.Builder;

@Builder
public record FreeBoardAnswerResponseDto(
        String answerId,
        UserResponseDto writer,
        String contents,
        String updatedDate
) {
}
