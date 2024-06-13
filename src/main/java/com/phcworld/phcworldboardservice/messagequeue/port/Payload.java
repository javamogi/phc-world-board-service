package com.phcworld.phcworldboardservice.messagequeue.port;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class Payload {
    private String board_id;
    private String writer_id;
//    private int count_of_answer;
    private byte is_deleted;
//    private long create_date;
    private String update_date;
    private String title;
    private String contents;
}
