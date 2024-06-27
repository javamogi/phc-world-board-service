package com.phcworld.phcworldboardservice.infrastructure.dto;

import com.phcworld.phcworldboardservice.domain.FreeBoard;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FreeBoardSelectDto {

//    private String boardId;
    private Long boardId;
    private String writerId;
    private String title;
    private String contents;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private Integer count;
    private Integer countOfAnswer;
    private Boolean isDeleted;

    public FreeBoard toModel() {
        return FreeBoard.builder()
                .id(boardId)
                .writerId(writerId)
                .title(title)
                .contents(contents)
                .createDate(createDate)
                .updateDate(updateDate)
                .count(count)
                .countOfAnswer(countOfAnswer)
                .isDeleted(isDeleted)
                .build();
    }
}
