package com.phcworld.phcworldboardservice.mock;


import com.phcworld.phcworldboardservice.domain.User;
import com.phcworld.phcworldboardservice.service.port.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FakeUserRepository implements UserRepository {
    private final List<User> data = new ArrayList<>();

    @Override
    public Optional<User> findById(String userId) {
        return data.stream().filter(user -> user.getUserId().equals(userId)).findAny();
    }

    @Override
    public User save(User user) {
        if(user.getUserId() == null){
            User newUser = User.builder()
                    .userId(user.getUserId())
                    .name(user.getName())
                    .build();
            data.add(newUser);
            return newUser;
        } else {
            data.removeIf(u -> Objects.equals(u.getUserId(), user.getUserId()));
            data.add(user);
            return user;
        }
    }
}
