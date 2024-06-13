package com.phcworld.phcworldboardservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class FreeBoard {
    private Long id;
    private String writerId;
    private String title;
    private String contents;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private int count;
    private boolean isDeleted;
    private int countOfAnswer;
}
