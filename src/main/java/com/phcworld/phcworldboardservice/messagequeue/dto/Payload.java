package com.phcworld.phcworldboardservice.messagequeue.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payload {
    private Long id;
    private String writer_id;
    private String board_id;
//    private int count_of_answer;
    private byte is_deleted;
//    private long create_date;
    private String update_date;
    private String title;
    private String contents;
    private Integer count;
    private Integer count_of_answer;
}
