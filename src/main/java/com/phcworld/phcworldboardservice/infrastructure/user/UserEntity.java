package com.phcworld.phcworldboardservice.infrastructure.user;

import com.phcworld.phcworldboardservice.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "BOARD_USERS")
public class UserEntity {
    @Id
    @Column(name = "id")
    private String userId;
    private String name;
    private String profileImage;

    public static UserEntity from(User user) {
        return UserEntity.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .build();
    }

    public User toModel() {
        return User.builder()
                .userId(userId)
                .name(name)
                .profileImage(profileImage)
                .build();
    }
}
