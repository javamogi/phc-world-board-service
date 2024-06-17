package com.phcworld.phcworldboardservice.service.port;

import com.phcworld.phcworldboardservice.domain.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(String userId);

    User save(User user);
}
