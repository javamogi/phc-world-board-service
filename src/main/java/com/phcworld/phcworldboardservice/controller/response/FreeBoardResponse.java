package com.phcworld.phcworldboardservice.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.service.dto.FreeBoardAnswerResponse;
import com.phcworld.phcworldboardservice.service.dto.UserResponse;
import com.phcworld.phcworldboardservice.utils.LocalDateTimeUtils;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FreeBoardResponse(
        Long id,
        String boardId,
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

    public static FreeBoardResponse of(FreeBoard freeBoard, UserResponse user){
        return FreeBoardResponse.builder()
                .id(freeBoard.getId())
                .boardId(freeBoard.getBoardId())
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
                .id(freeBoard.getId())
                .boardId(freeBoard.getBoardId())
                .title(freeBoard.getTitle())
                .contents(freeBoard.getContents())
                .isNew(freeBoard.isNew())
                .count(freeBoard.getCount())
                .countOfAnswer(freeBoard.getCountOfAnswer())
                .createDate(LocalDateTimeUtils.getTime(freeBoard.getCreateDate()))
                .isDelete(freeBoard.isDeleted())
                .build();
    }

    public static FreeBoardResponse of(FreeBoard freeBoard, UserResponse user, List<FreeBoardAnswerResponse> answers){
        return FreeBoardResponse.builder()
                .id(freeBoard.getId())
                .boardId(freeBoard.getBoardId())
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
