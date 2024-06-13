package com.phcworld.phcworldboardservice.domain.port;

import com.phcworld.phcworldboardservice.infrastructure.FreeBoardEntity;
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
//        public FreeBoardEntity toEntity(String boardId, String writerId){
//                return FreeBoardEntity.builder()
////                        .boardId(boardId)
//                        .writerId(writerId)
//                        .title(title)
//                        .contents(contents)
//                        .build();
//        }

        public FreeBoardEntity toEntity(String writerId){
                return FreeBoardEntity.builder()
                        .writerId(writerId)
                        .title(title)
                        .contents(contents)
                        .build();
        }
}
