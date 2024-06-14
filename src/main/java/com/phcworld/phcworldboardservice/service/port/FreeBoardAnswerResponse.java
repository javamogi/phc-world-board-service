package com.phcworld.phcworldboardservice.service.port;

import com.phcworld.phcworldboardservice.service.port.UserResponse;
import lombok.Builder;

@Builder
public record FreeBoardAnswerResponse(
        String answerId,
        UserResponse writer,
        String contents,
        String updatedDate
) {
}
