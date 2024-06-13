package com.phcworld.phcworldboardservice.controller.port;

import lombok.Builder;

@Builder
public record SuccessResponse(
        Integer statusCode,
        String message
) {
}
