package com.phcworld.phcworldboardservice.domain.port;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record FreeBoardRequest(
        Long id,
        @NotBlank(message = "제목을 입력하세요.")
        String title,
        @NotBlank(message = "내용을 입력하세요.")
        String contents
) {
}
