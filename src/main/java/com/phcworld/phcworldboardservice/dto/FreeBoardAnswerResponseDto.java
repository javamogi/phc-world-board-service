package com.phcworld.phcworldboardservice.dto;

import lombok.Builder;

@Builder
public record FreeBoardAnswerResponseDto(
        Long id,
        UserResponseDto writer,
        String contents,
        String updatedDate
) {
}
