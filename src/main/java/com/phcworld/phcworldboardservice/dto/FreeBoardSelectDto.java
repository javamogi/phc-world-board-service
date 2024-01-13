package com.phcworld.phcworldboardservice.dto;

import com.phcworld.phcworldboardservice.utils.LocalDateTimeUtils;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FreeBoardSelectDto {

    private String boardId;
    private String writerId;
    private String title;
    private String contents;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private Integer count;
    private Long countOfAnswer;

    public String getFormattedCreateDate() {
        return LocalDateTimeUtils.getTime(createDate);
    }

    public String getFormattedUpdateDate() {
        return LocalDateTimeUtils.getTime(updateDate);
    }

    public Boolean isNew(){
        final int HOUR_OF_DAY = 24;
        final int MINUTES_OF_HOUR = 60;

        long createdDateAndNowDifferenceMinutes =
                Duration.between(createDate == null ? LocalDateTime.now() : createDate, LocalDateTime.now()).toMinutes();
        return (createdDateAndNowDifferenceMinutes / MINUTES_OF_HOUR) < HOUR_OF_DAY;
    }

    public Integer getCountOfAnswer(){
        if(countOfAnswer == null){
            return 0;
        }
        return countOfAnswer.intValue();
    }

}
