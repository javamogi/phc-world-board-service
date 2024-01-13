package com.phcworld.phcworldboardservice.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class Payload {
    private String board_id;
    private String writer_id;
    private int count;
    private int count_of_answer;
    private int is_deleted;
    private long create_date;
    private long update_date;
    private String title;
    private String contents;
}
