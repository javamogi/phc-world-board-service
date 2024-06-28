package com.phcworld.phcworldboardservice.infrastructure;

import com.phcworld.phcworldboardservice.domain.FreeBoard;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FreeBoardRedisEntity implements Serializable {
    private String boardId;
    private String writerId;
    private String title;
    private String contents;
    private String createDate;
    private int count;
    private int countOfAnswer;
    private boolean isDelete;

    public static FreeBoardRedisEntity from(FreeBoard freeBoard, String boardId) {
        return FreeBoardRedisEntity.builder()
                .boardId(boardId)
                .writerId(freeBoard.getWriterId())
                .title(freeBoard.getTitle())
                .contents(freeBoard.getContents())
                .createDate(freeBoard.getCreateDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")))
                .count(freeBoard.getCount())
                .countOfAnswer(freeBoard.getCountOfAnswer())
                .isDelete(freeBoard.isDeleted())
                .build();
    }

    public FreeBoard toModel() {
        return FreeBoard.builder()
                .writerId(writerId)
                .title(title)
                .contents(contents)
                .createDate(LocalDateTime.parse(createDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")))
                .count(count)
                .countOfAnswer(countOfAnswer)
                .isDeleted(isDelete)
                .build();
    }
}
