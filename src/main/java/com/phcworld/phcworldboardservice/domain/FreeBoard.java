package com.phcworld.phcworldboardservice.domain;

import com.phcworld.phcworldboardservice.domain.port.FreeBoardRequest;
import com.phcworld.phcworldboardservice.service.port.LocalDateTimeHolder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class FreeBoard {
    private Long id;
//    private User user;
    private String writerId;
    private String title;
    private String contents;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private int count;
    private boolean isDeleted;
    private int countOfAnswer;
    Boolean isDeleteAuthority;
    Boolean isModifyAuthority;

    public static FreeBoard from(FreeBoardRequest request, String userId, LocalDateTimeHolder timeHolder) {
        return FreeBoard.builder()
                .writerId(userId)
                .title(request.title())
                .contents(request.contents())
                .createDate(timeHolder.now())
                .updateDate(timeHolder.now())
                .count(0)
                .countOfAnswer(0)
                .isDeleted(false)
                .build();
    }

    public Boolean isNew(){
        final int HOUR_OF_DAY = 24;
        final int MINUTES_OF_HOUR = 60;

        long createdDateAndNowDifferenceMinutes =
                Duration.between(createDate == null ? LocalDateTime.now() : createDate, LocalDateTime.now()).toMinutes();
        return (createdDateAndNowDifferenceMinutes / MINUTES_OF_HOUR) < HOUR_OF_DAY;
    }

    public FreeBoard setAuthority(String userId, Authority authority) {
        boolean isDeleteAuthority = false;
        boolean isModifyAuthority = false;

        if(!matchUser(userId)){
            isModifyAuthority = true;
            isDeleteAuthority = true;
        }
        if(authority == Authority.ROLE_ADMIN){
            isDeleteAuthority = true;
        }

        return FreeBoard.builder()
                .writerId(writerId)
                .title(title)
                .contents(contents)
                .createDate(createDate)
                .updateDate(updateDate)
                .count(count)
                .countOfAnswer(countOfAnswer)
                .isDeleted(false)
                .isDeleteAuthority(isDeleteAuthority)
                .isModifyAuthority(isModifyAuthority)
                .build();
    }

    public FreeBoard addCount() {
        return FreeBoard.builder()
                .writerId(writerId)
                .title(title)
                .contents(contents)
                .createDate(createDate)
                .updateDate(updateDate)
                .count(count + 1)
                .countOfAnswer(countOfAnswer)
                .isDeleted(false)
                .isDeleteAuthority(isDeleteAuthority)
                .isModifyAuthority(isModifyAuthority)
                .build();
    }

    public boolean matchUser(String userId) {
        return writerId.equals(userId);
    }

    public FreeBoard update(String title, String contents) {
        return FreeBoard.builder()
                .id(id)
                .writerId(writerId)
                .title(title)
                .contents(contents)
                .createDate(createDate)
                .updateDate(updateDate)
                .count(count)
                .countOfAnswer(countOfAnswer)
                .isDeleted(false)
                .build();
    }

    public FreeBoard delete() {
        return FreeBoard.builder()
                .id(id)
                .writerId(writerId)
                .title(title)
                .contents(contents)
                .createDate(createDate)
                .updateDate(updateDate)
                .count(count)
                .countOfAnswer(countOfAnswer)
                .isDeleted(true)
                .build();
    }

    public FreeBoard addCountOfAnswer() {
        return FreeBoard.builder()
                .id(id)
                .writerId(writerId)
                .title(title)
                .contents(contents)
                .createDate(createDate)
                .updateDate(updateDate)
                .count(count)
                .countOfAnswer(countOfAnswer + 1)
                .isDeleted(true)
                .build();
    }
}
