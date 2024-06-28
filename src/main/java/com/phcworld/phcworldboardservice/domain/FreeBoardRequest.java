package com.phcworld.phcworldboardservice.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record FreeBoardRequest(
        String boardId,
        @NotBlank(message = "제목을 입력하세요.")
        String title,
        @NotBlank(message = "내용을 입력하세요.")
        String contents
) {
}
