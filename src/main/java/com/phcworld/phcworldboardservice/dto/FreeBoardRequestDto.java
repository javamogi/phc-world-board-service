package com.phcworld.phcworldboardservice.dto;

import com.phcworld.phcworldboardservice.domain.FreeBoard;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record FreeBoardRequestDto(
        String id,
        @NotBlank(message = "제목을 입력하세요.")
        String title,
        @NotBlank(message = "내용을 입력하세요.")
        String contents
) {
        public FreeBoard toEntity(String boardId, String writerId){
                return FreeBoard.builder()
                        .boardId(boardId)
                        .writerId(writerId)
                        .title(title)
                        .contents(contents)
                        .build();
        }
}
