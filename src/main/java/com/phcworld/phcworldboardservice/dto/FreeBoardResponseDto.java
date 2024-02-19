package com.phcworld.phcworldboardservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FreeBoardResponseDto(
        String boardId,
        UserResponseDto writer,
        String title,
        String contents,
        String createDate,
        Integer count,
        Integer countOfAnswer,
        Boolean isNew,
        List<FreeBoardAnswerResponseDto> answers,
        Boolean isDeleteAuthority,
        Boolean isModifyAuthority
) {
    public static FreeBoardResponseDto of(UserResponseDto user, FreeBoard freeBoard){
        return FreeBoardResponseDto.builder()
                .boardId(freeBoard.getBoardId())
                .title(freeBoard.getTitle())
                .contents(freeBoard.getContents())
                .writer(user)
                .isNew(freeBoard.isNew())
                .count(freeBoard.getCount())
                .countOfAnswer(freeBoard.getCountOfAnswer())
                .build();
    }

}
