package com.phcworld.phcworldboardservice.controller;

import com.phcworld.phcworldboardservice.controller.port.FreeBoardResponse;
import com.phcworld.phcworldboardservice.controller.port.FreeBoardSearch;
import com.phcworld.phcworldboardservice.domain.Authority;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.domain.User;
import com.phcworld.phcworldboardservice.domain.port.FreeBoardRequest;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FreeBoardApiControllerTest {

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
        ResponseEntity<FreeBoardResponse> result = testContainer.freeBoardApiController.register(request, "1111");

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
            testContainer.freeBoardApiController.register(request, "9999");
        });
    }

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
        ResponseEntity<List<FreeBoardResponse>> result = testContainer.freeBoardApiController.getList(search, "token");

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
        ResponseEntity<List<FreeBoardResponse>> result = testContainer.freeBoardApiController.getList(search, "token");

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
        ResponseEntity<List<FreeBoardResponse>> result = testContainer.freeBoardApiController.getList(search, "token");

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
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(userId)
                .createDate(time)
                .updateDate(time)
                .isDeleted(false)
                .build());
        long id = 1;
        Authentication authentication = new FakeAuthentication(userId, "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        ResponseEntity<FreeBoardResponse> result = testContainer.freeBoardApiController.getFreeBoardWithAnswers(id, "1111");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().boardId()).isEqualTo(1);
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
        long freeBoardId = 1;
        Authentication authentication = new FakeAuthentication(userId, "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            testContainer.freeBoardApiController.getFreeBoardWithAnswers(freeBoardId, "token");
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
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(user)
                .createDate(time)
                .updateDate(time)
                .isDeleted(true)
                .build());
        long id = 1;
        Authentication authentication = new FakeAuthentication(user,"test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(DeletedEntityException.class, () -> {
            testContainer.freeBoardApiController.getFreeBoardWithAnswers(id, "token");
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
        ResponseEntity<FreeBoardResponse> result = testContainer.freeBoardApiController.update(request, "token");

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
            testContainer.freeBoardApiController.update(request, "token");
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
            testContainer.freeBoardApiController.update(request, "token");
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
            testContainer.freeBoardApiController.update(request, "token");
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
        ResponseEntity<FreeBoardResponse> result = testContainer.freeBoardApiController.delete(boardId, "token");

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
        ResponseEntity<FreeBoardResponse> result = testContainer.freeBoardApiController.delete(boardId, "token");

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
            testContainer.freeBoardApiController.delete(1L, "token");
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
            testContainer.freeBoardApiController.delete(1L, "token");
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
            testContainer.freeBoardApiController.delete(1L, "token");
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
        ResponseEntity<List<FreeBoardResponse>> result = testContainer.freeBoardApiController.getFreeBoardsByUser("1111");

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
        ResponseEntity<FreeBoardResponse> result = testContainer.freeBoardApiController.existFreeBoard(1L);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().boardId()).isEqualTo(1);
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
            testContainer.freeBoardApiController.existFreeBoard(1L);
        });
    }

}