package com.phcworld.phcworldboardservice.controller;

import com.phcworld.phcworldboardservice.infrastructure.dto.FreeBoardSearch;
import com.phcworld.phcworldboardservice.controller.response.FreeBoardResponse;
import com.phcworld.phcworldboardservice.domain.Authority;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.exception.model.DeletedEntityException;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FreeBoardQueryApiControllerTest {

    @Test
    @DisplayName("제목으로 검색해서 게시글 목록을 가져올 수 있다.")
    void getListWhenSearchTitle(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        String user = "1111";

        testContainer.freeBoardRepository.save(FreeBoard.builder()
                .id(1L)
                .boardId("board-1")
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build());
        testContainer.freeBoardRepository.save(FreeBoard.builder()
                .id(2L)
                .boardId("board-2")
                .title("안녕하세요")
                .contents("잘부탁드립니다")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build());
        FreeBoardSearch search = FreeBoardSearch.builder()
                .searchType(0)
                .keyword("제목")
                .pageNum(1)
                .pageSize(5)
                .build();

        // when
        ResponseEntity<List<FreeBoardResponse>> result = testContainer.freeBoardQueryApiController.getList(search, "token");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("내용으로 검색해서 게시글 목록을 가져올 수 있다.")
    void getListWhenSearchContent(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        String user = "1111";
        testContainer.freeBoardRepository.save(FreeBoard.builder()
                .id(1L)
                .boardId("board-1")
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build());
        testContainer.freeBoardRepository.save(FreeBoard.builder()
                .id(2L)
                .boardId("board-2")
                .title("안녕하세요")
                .contents("잘부탁드립니다")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build());
        FreeBoardSearch search = FreeBoardSearch.builder()
                .searchType(1)
                .keyword("내용")
                .pageNum(1)
                .pageSize(5)
                .build();

        // when
        ResponseEntity<List<FreeBoardResponse>> result = testContainer.freeBoardQueryApiController.getList(search, "token");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("작성자 이름으로 검색해서 게시글 목록을 가져올 수 있다.")
    void getListWhenSearchWriterName(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        String user = "1111";
        testContainer.freeBoardRepository.save(FreeBoard.builder()
                .id(1L)
                .boardId("board-1")
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build());
        testContainer.freeBoardRepository.save(FreeBoard.builder()
                .id(2L)
                .boardId("board-2")
                .title("안녕하세요")
                .contents("잘부탁드립니다")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build());
        FreeBoardSearch search = FreeBoardSearch.builder()
                .searchType(3)
                .keyword("테스트")
                .pageNum(1)
                .pageSize(5)
                .build();

        // when
        ResponseEntity<List<FreeBoardResponse>> result = testContainer.freeBoardQueryApiController.getList(search, "token");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).hasSize(2);
    }

    @Test
    @DisplayName("회원은 게시글의 id로 게시글을 가져올 수 있다.")
    void getFreeBoard(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        String userId = "1111";
        testContainer.freeBoardRepository.save(FreeBoard.builder()
                .id(1L)
                .boardId("board-1")
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(userId)
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build());
        String id = "board-1";
        Authentication authentication = new FakeAuthentication(userId, "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        ResponseEntity<FreeBoardResponse> result = testContainer.freeBoardQueryApiController.getFreeBoardWithAnswers(id, "1111");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().boardId()).isEqualTo("board-1");
        assertThat(result.getBody().title()).isEqualTo("제목");
        assertThat(result.getBody().contents()).isEqualTo("내용");
        assertThat(result.getBody().count()).isEqualTo(1);
        assertThat(result.getBody().isNew()).isTrue();
        assertThat(result.getBody().countOfAnswer()).isZero();
        assertThat(result.getBody().createDate()).isEqualTo(LocalDateTimeUtils.getTime(time));
    }

    @Test
    @DisplayName("id의 게시글이 없는 경우 게시글을 가져올 수 없다.")
    void failedGetFreeBoardWhenNotFoundFreeBoard(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        String userId = "1111";
        String freeBoardId = "board-1";
        Authentication authentication = new FakeAuthentication(userId, "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            testContainer.freeBoardQueryApiController.getFreeBoardWithAnswers(freeBoardId, "token");
        });
    }

    @Test
    @DisplayName("id의 게시글이 삭제된 경우 게시글을 가져올 수 없다.")
    void failedGetFreeBoardWhenDeletedFreeBoard(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        String user = "1111";
        testContainer.freeBoardRepository.save(FreeBoard.builder()
                .id(1L)
                .boardId("board-1")
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleted(true)
                .build());
        String id = "board-1";
        Authentication authentication = new FakeAuthentication(user,"test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(DeletedEntityException.class, () -> {
            testContainer.freeBoardQueryApiController.getFreeBoardWithAnswers(id, "token");
        });
    }

    @Test
    @DisplayName("작성자 ID로 작성자가 등록한 게시글 목록을 가져올 수 있다.")
    void getListByUserId(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        String user = "1111";

        testContainer.freeBoardRepository.save(FreeBoard.builder()
                .id(1L)
                .boardId("board-1")
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build());
        testContainer.freeBoardRepository.save(FreeBoard.builder()
                .id(2L)
                .boardId("board-2")
                .title("안녕하세요")
                .contents("잘부탁드립니다")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build());
        Authentication authentication = new FakeAuthentication("1111","test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        ResponseEntity<List<FreeBoardResponse>> result = testContainer.freeBoardQueryApiController.getFreeBoardsByUser("1111");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).hasSize(2);
    }

    @Test
    @DisplayName("게시글 ID로 조회")
    void existBoard(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();
        String user = "1111";

        testContainer.freeBoardRepository.save(FreeBoard.builder()
                .id(1L)
                .boardId("board-1")
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

        // when
        ResponseEntity<FreeBoardResponse> result = testContainer.freeBoardQueryApiController.existFreeBoard("board-1");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().boardId()).isEqualTo("board-1");
        assertThat(result.getBody().title()).isEqualTo("제목");
        assertThat(result.getBody().contents()).isEqualTo("내용");
        assertThat(result.getBody().countOfAnswer()).isZero();
        assertThat(result.getBody().count()).isZero();
        assertThat(result.getBody().isDelete()).isFalse();
    }

    @Test
    @DisplayName("게시글 ID로 조회 실패")
    void failedExistBoardWhenNotFound(){
        // given
        LocalDateTime time = LocalDateTime.now();
        TestContainer testContainer = TestContainer.builder()
                .localDateTimeHolder(new FakeLocalDateTimeHolder(time))
                .build();

        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            testContainer.freeBoardQueryApiController.existFreeBoard("board-1");
        });
    }

}