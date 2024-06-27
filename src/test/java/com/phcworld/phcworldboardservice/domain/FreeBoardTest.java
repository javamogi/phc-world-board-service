package com.phcworld.phcworldboardservice.domain;

import com.phcworld.phcworldboardservice.mock.FakeLocalDateTimeHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FreeBoardTest {

    @Test
    @DisplayName("FreeBoardRequest 요청 정보로 생성할 수 있다.")
    void createByFreeBoardRequest(){
        // given
        LocalDateTime time = LocalDateTime.of(2024, 6, 14, 11, 11, 11, 111111);
        FreeBoardRequest freeBoardRequest = FreeBoardRequest.builder()
                .title("제목")
                .contents("내용")
                .build();
        String user = "1111";

        // when
        FreeBoard result = FreeBoard.from(freeBoardRequest, user, new FakeLocalDateTimeHolder(time));

        // then
        assertThat(result.getId()).isNull();
        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.getContents()).isEqualTo("내용");
        assertThat(result.getCount()).isZero();
        assertThat(result.getCountOfAnswer()).isZero();
        assertThat(result.isDeleted()).isFalse();
        assertThat(result.getCreateDate()).isEqualTo(time);
        assertThat(result.getUpdateDate()).isEqualTo(time);
    }

    @Test
    @DisplayName("게시글의 등록일이 24시간 내라면 새로운 글이다.")
    void g(){
        // given
        LocalDateTime time = LocalDateTime.now();
        String user = "1111";
        FreeBoard freeBoard = FreeBoard.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleteAuthority(true)
                .isModifyAuthority(true)
                .isDeleted(false)
                .build();

        // when
        boolean result = freeBoard.isNew();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("작성자 ID값으로 작성자가 같은지 확인할 수 있다.")
    void matchWriter(){
        // given
        LocalDateTime time = LocalDateTime.of(2024, 6, 14, 11, 11, 11, 111111);
        String user = "1111";
        FreeBoard freeBoard = FreeBoard.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleteAuthority(true)
                .isModifyAuthority(true)
                .isDeleted(false)
                .build();

        // when
        boolean result = freeBoard.matchUser("1111");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("조회수를 1 올린다.")
    void addCount(){
        // given
        LocalDateTime time = LocalDateTime.of(2024, 6, 14, 11, 11, 11, 111111);
        String user = "1111";
        FreeBoard freeBoard = FreeBoard.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleteAuthority(true)
                .isModifyAuthority(true)
                .isDeleted(false)
                .build();

        // when
        FreeBoard result = freeBoard.addCount();

        // then
        assertThat(result.getCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("제목과 내용을 수정할 수 있다.")
    void update(){
        // given
        LocalDateTime time = LocalDateTime.of(2024, 6, 14, 11, 11, 11, 111111);
        String user = "1111";
        FreeBoard freeBoard = FreeBoard.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleteAuthority(true)
                .isModifyAuthority(true)
                .isDeleted(false)
                .build();

        // when
        FreeBoard result = freeBoard.update("수정된 제목", "수정된 내용", new FakeLocalDateTimeHolder(time));

        // then
        assertThat(result.getTitle()).isEqualTo("수정된 제목");
        assertThat(result.getContents()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("삭제 할 수 있다.(soft delete-논리삭제)")
    void delete(){
        // given
        LocalDateTime time = LocalDateTime.of(2024, 6, 14, 11, 11, 11, 111111);
        String user = "1111";
        FreeBoard freeBoard = FreeBoard.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleteAuthority(true)
                .isModifyAuthority(true)
                .isDeleted(false)
                .build();

        // when
        FreeBoard result = freeBoard.delete();

        // then
        assertThat(result.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("작성자는 수정,삭제 권한을 가질수 있다.")
    void getAuthoritiesWhenEqualWriter(){
        // given
        LocalDateTime time = LocalDateTime.of(2024, 6, 14, 11, 11, 11, 111111);
        String user = "1111";
        FreeBoard freeBoard = FreeBoard.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleteAuthority(false)
                .isModifyAuthority(false)
                .isDeleted(false)
                .build();

        // when
        FreeBoard result = freeBoard.setAuthority("1111", Authority.ROLE_USER);

        // then
        assertThat(result.getIsModifyAuthority()).isTrue();
        assertThat(result.getIsDeleteAuthority()).isTrue();
    }

    @Test
    @DisplayName("관리자는 삭제 권한을 가질수 있다.")
    void getAuthoritiesWhenAdmin(){
        // given
        LocalDateTime time = LocalDateTime.of(2024, 6, 14, 11, 11, 11, 111111);
        String user = "1111";
        FreeBoard freeBoard = FreeBoard.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleteAuthority(false)
                .isModifyAuthority(false)
                .isDeleted(false)
                .build();

        // when
        FreeBoard result = freeBoard.setAuthority("2222", Authority.ROLE_ADMIN);

        // then
        assertThat(result.getIsModifyAuthority()).isFalse();
        assertThat(result.getIsDeleteAuthority()).isTrue();
    }

    @Test
    @DisplayName("답변 개수를 1올린다.")
    void addCountOfAnswer(){
        // given
        LocalDateTime time = LocalDateTime.of(2024, 6, 14, 11, 11, 11, 111111);
        String user = "1111";
        FreeBoard freeBoard = FreeBoard.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleteAuthority(true)
                .isModifyAuthority(true)
                .isDeleted(false)
                .build();

        // when
        FreeBoard result = freeBoard.addCountOfAnswer();

        // then
        assertThat(result.getCountOfAnswer()).isEqualTo(1);
    }
}