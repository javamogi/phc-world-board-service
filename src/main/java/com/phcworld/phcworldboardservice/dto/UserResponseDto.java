package com.phcworld.phcworldboardservice.dto;

import lombok.Builder;

@Builder
public record UserResponseDto(
        String email,
        String name,
        String createDate,
        String profileImage,
        String userId
) {
}
