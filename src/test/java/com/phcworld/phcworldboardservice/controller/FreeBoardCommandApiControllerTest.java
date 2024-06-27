package com.phcworld.phcworldboardservice.controller;

import com.phcworld.phcworldboardservice.controller.response.FreeBoardResponse;
import com.phcworld.phcworldboardservice.domain.Authority;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.domain.FreeBoardRequest;
import com.phcworld.phcworldboardservice.exception.model.DeletedEntityException;
import com.phcworld.phcworldboardservice.exception.model.ForbiddenException;
import com.phcworld.phcworldboardservice.exception.model.NotFoundException;
import com.phcworld.phcworldboardservice.mock.FakeAuthentication;
import com.phcworld.phcworldboardservice.mock.FakeLocalDateTimeHolder;
import com.phcworld.phcworldboardservice.mock.TestContainer;
import com.phcworld.phcworldboardservice.utils.LocalDateTimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FreeBoardCommandApiControllerTest {

    @Test
    @DisplayName("회원은 게시글을 등록할 수 있다.")
    void register(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        FreeBoardRequest request = FreeBoardRequest.builder()
                .title("제목")
                .contents("내용")
                .build();
        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        ResponseEntity<FreeBoardResponse> result = testContainer.freeBoardCommandApiController.register(request, "1111");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().title()).isEqualTo("제목");
        assertThat(result.getBody().contents()).isEqualTo("내용");
        assertThat(result.getBody().count()).isZero();
        assertThat(result.getBody().countOfAnswer()).isZero();
        assertThat(result.getBody().createDate()).isEqualTo(LocalDateTimeUtils.getTime(time));
        assertThat(result.getBody().isNew()).isTrue();
    }

    @Test
    @DisplayName("가입하지 않은 회원은 게시글을 등록할 수 없다.")
    void failedRegisterWhenNotFoundUser(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        FreeBoardRequest request = FreeBoardRequest.builder()
                .title("제목")
                .contents("내용")
                .build();
        Authentication authentication = new FakeAuthentication("9999", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            testContainer.freeBoardCommandApiController.register(request, "9999");
        });
    }

    @Test
    @DisplayName("작성자는 게시글을 변경할 수 있다.")
    void update(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        String userId = "1111";
        testContainer.freeBoardRepository.save(FreeBoard.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(userId)
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build());
        FreeBoardRequest request = FreeBoardRequest.builder()
                .id(1L)
                .title("제목수정")
                .contents("내용수정")
                .build();
        Authentication authentication = new FakeAuthentication(userId, "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        ResponseEntity<FreeBoardResponse> result = testContainer.freeBoardCommandApiController.update(request, "token");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().boardId()).isEqualTo(1);
        assertThat(result.getBody().title()).isEqualTo("제목수정");
        assertThat(result.getBody().contents()).isEqualTo("내용수정");
        assertThat(result.getBody().count()).isZero();
        assertThat(result.getBody().isNew()).isTrue();
        assertThat(result.getBody().countOfAnswer()).isZero();
        assertThat(result.getBody().createDate()).isEqualTo(LocalDateTimeUtils.getTime(time));
    }

    @Test
    @DisplayName("등록되지 않은 게시물은 수정할 수 없다.")
    void failedUpdateWhenNotFound(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        String userId = "1111";
        FreeBoardRequest request = FreeBoardRequest.builder()
                .id(1L)
                .title("제목수정")
                .contents("내용수정")
                .build();
        Authentication authentication = new FakeAuthentication(userId,"test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            testContainer.freeBoardCommandApiController.update(request, "token");
        });
    }

    @Test
    @DisplayName("이미 삭제된 게시물은 수정할 수 없다.")
    void failedUpdateWhenDeleted(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        String user = "1111";
        testContainer.freeBoardRepository.save(FreeBoard.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleted(true)
                .build());
        FreeBoardRequest request = FreeBoardRequest.builder()
                .id(1L)
                .title("제목수정")
                .contents("내용수정")
                .build();
        Authentication authentication = new FakeAuthentication("1111","test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(DeletedEntityException.class, () -> {
            testContainer.freeBoardCommandApiController.update(request, "token");
        });
    }

    @Test
    @DisplayName("작성자가 다르면 수정할 수 없다.")
    void failedUpdateWhenNotEqualWriter(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        String user = "1111";
        testContainer.freeBoardRepository.save(FreeBoard.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build());
        FreeBoardRequest request = FreeBoardRequest.builder()
                .id(1L)
                .title("제목수정")
                .contents("내용수정")
                .build();
        Authentication authentication = new FakeAuthentication("2222","test2", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(ForbiddenException.class, () -> {
            testContainer.freeBoardCommandApiController.update(request, "token");
        });
    }

    @Test
    @DisplayName("작성자는 삭제할 수 있다.")
    void delete(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        String user = "1111";
        testContainer.freeBoardRepository.save(FreeBoard.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build());
        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        long boardId = 1;

        // when
        ResponseEntity<FreeBoardResponse> result = testContainer.freeBoardCommandApiController.delete(boardId, "token");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().isDelete()).isTrue();
    }

    @Test
    @DisplayName("관리자는 삭제할 수 있다.")
    void deleteByAdmin(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        String user = "1111";
        testContainer.freeBoardRepository.save(FreeBoard.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build());
        Authentication authentication = new FakeAuthentication("2222", "test2", Authority.ROLE_ADMIN).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        long boardId = 1;

        // when
        ResponseEntity<FreeBoardResponse> result = testContainer.freeBoardCommandApiController.delete(boardId, "token");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().isDelete()).isTrue();
    }

    @Test
    @DisplayName("등록되지 않은 게시물은 삭제할 수 없다.")
    void failedDelteWhenNotFound(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        Authentication authentication = new FakeAuthentication("1111","test", Authority.ROLE_ADMIN).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            testContainer.freeBoardCommandApiController.delete(1L, "token");
        });
    }

    @Test
    @DisplayName("이미 삭제된 게시물은 삭제할 수 없다.")
    void failedDeleteWhenDeleted(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        String user = "1111";
        testContainer.freeBoardRepository.save(FreeBoard.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleted(true)
                .build());
        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_ADMIN).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(DeletedEntityException.class, () -> {
            testContainer.freeBoardCommandApiController.delete(1L, "token");
        });
    }

    @Test
    @DisplayName("작성자가 다르면 삭제할 수 없다.")
    void failedDeletedWhenNotEqualWriter(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        String user = "1111";
        testContainer.freeBoardRepository.save(FreeBoard.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build());
        Authentication authentication = new FakeAuthentication("2222","test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(ForbiddenException.class, () -> {
            testContainer.freeBoardCommandApiController.delete(1L, "token");
        });
    }

}