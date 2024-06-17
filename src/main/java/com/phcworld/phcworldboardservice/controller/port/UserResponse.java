package com.phcworld.phcworldboardservice.controller.port;

import com.phcworld.phcworldboardservice.domain.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserResponse {
    private String userId;
    private String name;
    private String profileImage;

    public static UserResponse of(User writer) {
        return UserResponse.builder()
                .userId(writer.getUserId())
                .name(writer.getName())
                .profileImage(writer.getProfileImage())
                .build();
    }
}
