package com.phcworld.phcworldboardservice.infrastructure;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRedisEntity implements Serializable {
    private String email;
    private String name;
    private String createDate;
    private String profileImage;
    private String userId;
    private boolean isDelete;
    private Authority authority;
    private String password;
}
