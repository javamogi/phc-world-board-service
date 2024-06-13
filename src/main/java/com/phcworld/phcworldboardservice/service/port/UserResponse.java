package com.phcworld.phcworldboardservice.service.port;

import lombok.Builder;

@Builder
public record UserResponse(
        String email,
        String name,
        String createDate,
        String profileImage,
        String userId
) {
}
