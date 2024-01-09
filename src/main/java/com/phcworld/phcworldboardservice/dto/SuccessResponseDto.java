package com.phcworld.phcworldboardservice.dto;

import lombok.Builder;

@Builder
public record SuccessResponseDto(
        Integer statusCode,
        String message
) {
}
