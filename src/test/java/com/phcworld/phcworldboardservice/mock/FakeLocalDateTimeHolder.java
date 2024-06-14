package com.phcworld.phcworldboardservice.mock;

import com.phcworld.phcworldboardservice.service.port.LocalDateTimeHolder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class FakeLocalDateTimeHolder implements LocalDateTimeHolder {

    private final LocalDateTime now;

    @Override
    public LocalDateTime now() {
        return now;
    }
}
