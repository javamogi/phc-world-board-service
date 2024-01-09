package com.phcworld.phcworldboardservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FreeBoardResponseDto(
        Long id,
        UserResponseDto writer,
        String title,
        String contents,
        String createDate,
        Integer count,
        Integer countOfAnswer,
        Boolean isNew,
        List<FreeBoardAnswerResponseDto> answers
) {
}
