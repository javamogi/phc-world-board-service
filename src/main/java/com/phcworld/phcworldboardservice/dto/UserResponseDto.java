package com.phcworld.phcworldboardservice.dto;

import lombok.Builder;

@Builder
public record UserResponseDto(
        Long id,
        String email,
        String name,
        String createDate,
        String profileImage,
        String userId
) {
}
