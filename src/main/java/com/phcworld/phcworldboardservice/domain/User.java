package com.phcworld.phcworldboardservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class User {
    private String userId;
    private String name;
    private String profileImage;

    public User update(String name) {
        return User.builder()
                .userId(userId)
                .name(name)
                .profileImage(profileImage)
                .build();
    }
}
