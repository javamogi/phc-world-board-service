package com.phcworld.phcworldboardservice.controller.port;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.service.port.FreeBoardAnswerResponse;
import com.phcworld.phcworldboardservice.service.port.UserResponse;
import com.phcworld.phcworldboardservice.utils.LocalDateTimeUtils;
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
        Boolean isDelete,
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
                .createDate(LocalDateTimeUtils.getTime(freeBoard.getCreateDate()))
                .isDelete(freeBoard.isDeleted())
                .build();
    }

    public static FreeBoardResponse of(FreeBoard freeBoard){
        return FreeBoardResponse.builder()
                .boardId(freeBoard.getId())
                .title(freeBoard.getTitle())
                .contents(freeBoard.getContents())
                .isNew(freeBoard.isNew())
                .count(freeBoard.getCount())
                .countOfAnswer(freeBoard.getCountOfAnswer())
                .createDate(LocalDateTimeUtils.getTime(freeBoard.getCreateDate()))
                .isDelete(freeBoard.isDeleted())
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
                .createDate(LocalDateTimeUtils.getTime(freeBoard.getCreateDate()))
                .isDelete(freeBoard.isDeleted())
                .build();
    }

}
