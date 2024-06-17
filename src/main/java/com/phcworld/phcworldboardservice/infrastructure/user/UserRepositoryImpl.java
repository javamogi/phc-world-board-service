package com.phcworld.phcworldboardservice.infrastructure.user;

import com.phcworld.phcworldboardservice.domain.User;
import com.phcworld.phcworldboardservice.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(String userId) {
        return userJpaRepository.findById(userId)
                .map(UserEntity::toModel);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(UserEntity.from(user)).toModel();
    }
}
