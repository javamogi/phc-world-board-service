package com.phcworld.phcworldboardservice.controller.port;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.phcworld.phcworldboardservice.infrastructure.FreeBoardEntity;
import com.phcworld.phcworldboardservice.infrastructure.port.FreeBoardAnswerResponse;
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
    public static FreeBoardResponse of(UserResponse user, FreeBoardEntity freeBoardEntity){
        return FreeBoardResponse.builder()
                .boardId(freeBoardEntity.getId())
                .title(freeBoardEntity.getTitle())
                .contents(freeBoardEntity.getContents())
                .writer(user)
                .isNew(freeBoardEntity.isNew())
                .count(freeBoardEntity.getCount())
                .countOfAnswer(freeBoardEntity.getCountOfAnswer())
                .build();
    }

}
