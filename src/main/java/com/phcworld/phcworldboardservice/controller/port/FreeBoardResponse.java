package com.phcworld.phcworldboardservice.controller.port;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.service.port.FreeBoardAnswerResponse;
import com.phcworld.phcworldboardservice.service.port.UserResponse;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FreeBoardResponse(
        Long boardId,
        UserResponse writer,
        String title,
        String contents,
        String createDate,
        Integer count,
        Integer countOfAnswer,
        Boolean isNew,
        List<FreeBoardAnswerResponse> answers,
        Boolean isDeleteAuthority,
        Boolean isModifyAuthority
) {

    public static FreeBoardResponse of(UserResponse user, FreeBoard freeBoard){
        return FreeBoardResponse.builder()
                .boardId(freeBoard.getId())
                .title(freeBoard.getTitle())
                .contents(freeBoard.getContents())
                .writer(user)
                .isNew(freeBoard.isNew())
                .count(freeBoard.getCount())
                .countOfAnswer(freeBoard.getCountOfAnswer())
                .build();
    }

    public static FreeBoardResponse of(FreeBoard freeBoard){
        return FreeBoardResponse.builder()
                .boardId(freeBoard.getId())
                .title(freeBoard.getTitle())
                .contents(freeBoard.getContents())
                .writer(null)
                .isNew(freeBoard.isNew())
                .count(freeBoard.getCount())
                .countOfAnswer(freeBoard.getCountOfAnswer())
                .build();
    }

    public static FreeBoardResponse of(UserResponse user, FreeBoard freeBoard, List<FreeBoardAnswerResponse> answers){
        return FreeBoardResponse.builder()
                .boardId(freeBoard.getId())
                .title(freeBoard.getTitle())
                .contents(freeBoard.getContents())
                .writer(user)
                .isNew(freeBoard.isNew())
                .count(freeBoard.getCount())
                .countOfAnswer(freeBoard.getCountOfAnswer())
                .answers(answers)
                .build();
    }

}
