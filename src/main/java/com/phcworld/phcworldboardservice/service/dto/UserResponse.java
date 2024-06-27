package com.phcworld.phcworldboardservice.service.dto;

import com.phcworld.phcworldboardservice.domain.User;
import lombok.Builder;

@Builder
public record UserResponse(
//        String email,
//        String createDate,
        String name,
        String profileImage,
        String userId
) {
    public static UserResponse of(User writer) {
        return UserResponse.builder()
                .name(writer.getName())
                .profileImage(writer.getProfileImage())
                .userId(writer.getUserId())
                .build();
    }
}
