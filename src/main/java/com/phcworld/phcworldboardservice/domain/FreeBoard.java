package com.phcworld.phcworldboardservice.domain;

import com.phcworld.phcworldboardservice.service.port.LocalDateTimeHolder;
import com.phcworld.phcworldboardservice.service.port.UuidHolder;
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

    private String boardId;
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

    public Boolean isNew(){
        final int HOUR_OF_DAY = 24;
        final int MINUTES_OF_HOUR = 60;

        long createdDateAndNowDifferenceMinutes =
                Duration.between(createDate == null ? LocalDateTime.now() : createDate, LocalDateTime.now()).toMinutes();
        return (createdDateAndNowDifferenceMinutes / MINUTES_OF_HOUR) < HOUR_OF_DAY;
    }

    public boolean matchUser(String userId) {
        return this.writerId.equals(userId);
    }

    public static FreeBoard from(FreeBoardRequest request,
                                 String userId,
                                 LocalDateTimeHolder timeHolder,
                                 UuidHolder uuidHolder) {
        return FreeBoard.builder()
                .boardId(uuidHolder.random())
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

    public FreeBoard addCount() {
        return FreeBoard.builder()
                .id(id)
                .boardId(boardId)
                .writerId(writerId)
                .title(title)
                .contents(contents)
                .createDate(createDate)
                .updateDate(updateDate)
                .count(count + 1)
                .countOfAnswer(countOfAnswer)
                .isDeleted(isDeleted)
                .isDeleteAuthority(isDeleteAuthority)
                .isModifyAuthority(isModifyAuthority)
                .build();
    }

    public FreeBoard update(String title, String contents, LocalDateTimeHolder timeHolder) {
        return FreeBoard.builder()
                .id(id)
                .boardId(boardId)
                .writerId(writerId)
                .title(title)
                .contents(contents)
                .createDate(createDate)
                .updateDate(timeHolder.now())
                .count(count)
                .countOfAnswer(countOfAnswer)
                .isDeleted(isDeleted)
                .build();
    }

    public FreeBoard delete() {
        return FreeBoard.builder()
                .id(id)
                .boardId(boardId)
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

    public FreeBoard setAuthority(String userId, Authority authority) {
        boolean isDeleteAuthority = false;
        boolean isModifyAuthority = false;

        if(matchUser(userId)){
            isModifyAuthority = true;
            isDeleteAuthority = true;
        }
        if(authority == Authority.ROLE_ADMIN){
            isDeleteAuthority = true;
        }

        return FreeBoard.builder()
                .id(id)
                .boardId(boardId)
                .writerId(writerId)
                .title(title)
                .contents(contents)
                .createDate(createDate)
                .updateDate(updateDate)
                .count(count)
                .countOfAnswer(countOfAnswer)
                .isDeleted(isDeleted)
                .isDeleteAuthority(isDeleteAuthority)
                .isModifyAuthority(isModifyAuthority)
                .build();
    }

    public FreeBoard addCountOfAnswer() {
        return FreeBoard.builder()
                .id(id)
                .boardId(boardId)
                .writerId(writerId)
                .title(title)
                .contents(contents)
                .createDate(createDate)
                .updateDate(updateDate)
                .count(count)
                .countOfAnswer(countOfAnswer + 1)
                .isDeleted(isDeleted)
                .build();
    }
}
