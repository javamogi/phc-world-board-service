package com.phcworld.phcworldboardservice.service;

import com.phcworld.phcworldboardservice.controller.port.FreeBoardSearch;
import com.phcworld.phcworldboardservice.domain.Authority;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.domain.User;
import com.phcworld.phcworldboardservice.domain.port.FreeBoardRequest;
import com.phcworld.phcworldboardservice.exception.model.DeletedEntityException;
import com.phcworld.phcworldboardservice.exception.model.ForbiddenException;
import com.phcworld.phcworldboardservice.exception.model.NotFoundException;
import com.phcworld.phcworldboardservice.mock.*;
import com.phcworld.phcworldboardservice.service.port.LocalDateTimeHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class FreeBoardServiceImplTest {

    private FreeBoardServiceImpl freeBoardService;

    @BeforeEach
    void init(){
        LocalDateTime time = LocalDateTime.of(2024, 6, 14, 11, 11, 11, 111111);
        FakeFreeBoardRepository fakeFreeBoardRepository = new FakeFreeBoardRepository();
        LocalDateTimeHolder localDateTimeHolder = new FakeLocalDateTimeHolder(time);
        this.freeBoardService = FreeBoardServiceImpl.builder()
                .freeBoardRepository(fakeFreeBoardRepository)
                .boardProducer(new FakeKafkaProducer())
                .localDateTimeHolder(localDateTimeHolder)
                .build();
        String userId1 = "1111";
        String userId2 = "2222";

        fakeFreeBoardRepository.save(FreeBoard.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId(userId1)
                .createDate(time)
                .updateDate(time)
                .isDeleteAuthority(true)
                .isModifyAuthority(true)
                .isDeleted(false)
                .build());

        fakeFreeBoardRepository.save(FreeBoard.builder()
                .id(2L)
                .title("제목2")
                .contents("내용2")
                .countOfAnswer(0)
                .count(0)
                .writerId(userId1)
                .createDate(time)
                .updateDate(time)
                .isDeleteAuthority(false)
                .isModifyAuthority(false)
                .isDeleted(false)
                .build());

        fakeFreeBoardRepository.save(FreeBoard.builder()
                .id(3L)
                .title("안녕하세요.")
                .contents("잘부탁드립니다.")
                .countOfAnswer(0)
                .count(0)
                .writerId(userId1)
                .createDate(time)
                .updateDate(time)
                .isDeleteAuthority(true)
                .isModifyAuthority(false)
                .isDeleted(false)
                .build());
        fakeFreeBoardRepository.save(FreeBoard.builder()
                .id(4L)
                .title("삭제테스트")
                .contents("삭제테스트를위한데이터")
                .countOfAnswer(0)
                .count(0)
                .writerId(userId2)
                .createDate(time)
                .updateDate(time)
                .isDeleteAuthority(false)
                .isModifyAuthority(false)
                .isDeleted(true)
                .build());
    }

    @Test
    @DisplayName("Token으로 받은 아이디로 게시글을 등록할 수 있다.")
    void successRegister(){
        // given
        FreeBoardRequest request = FreeBoardRequest.builder()
                .title("제목")
                .contents("내용")
                .build();
        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        FreeBoard result = freeBoardService.register(request);

        // then
        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.getContents()).isEqualTo("내용");
        assertThat(result.getCount()).isZero();
        assertThat(result.getCountOfAnswer()).isZero();
        assertThat(result.isDeleted()).isFalse();
        assertThat(result.getWriterId()).isEqualTo("1111");
    }

    @Test
    @DisplayName("제목으로 검색해서 게시글 목록을 가져올 수 있다.")
    void getSearchListWhenSearchTitle(){
        // given
        FreeBoardSearch search = FreeBoardSearch.builder()
                .searchType(0)
                .keyword("제목")
                .pageNum(1)
                .pageSize(5)
                .build();

        // when
        List<FreeBoard> result = freeBoardService.getSearchList(search);

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getTitle()).isEqualTo("제목");
        assertThat(result.get(1).getTitle()).isEqualTo("제목2");
    }

    @Test
    @DisplayName("내용으로 검색해서 게시글 목록을 가져올 수 있다.")
    void getSearchListWhenSearchContent(){
        // given
        FreeBoardSearch search = FreeBoardSearch.builder()
                .searchType(1)
                .keyword("잘부탁")
                .pageNum(1)
                .pageSize(5)
                .build();

        // when
        List<FreeBoard> result = freeBoardService.getSearchList(search);

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTitle()).isEqualTo("안녕하세요.");
    }

    @Test
    @DisplayName("회원은 게시글의 id로 게시글을 가져올 수 있다.")
    void getFreeBoard(){
        // given
        long freeBoardId = 1;
        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        FreeBoard result = freeBoardService.getFreeBoard(freeBoardId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(freeBoardId);
        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.getContents()).isEqualTo("내용");
        assertThat(result.getCount()).isEqualTo(1);
        assertThat(result.getWriterId()).isEqualTo("1111");
        assertThat(result.getCreateDate()).isEqualTo(LocalDateTime.of(2024, 6, 14, 11, 11, 11, 111111));
        assertThat(result.getIsModifyAuthority()).isTrue();
        assertThat(result.getIsDeleteAuthority()).isTrue();
    }

    @Test
    @DisplayName("id의 게시글이 없는 경우 게시글을 가져올 수 없다.")
    void failedGetFreeBoardWhenNotFoundFreeBoard(){
        // given
        long id = 999;

        // when
        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            freeBoardService.getFreeBoard(id);
        });
    }

    @Test
    @DisplayName("id의 게시글이 삭제된 경우 게시글을 가져올 수 없다.")
    void failedGetFreeBoardWhenDeletedFreeBoard(){
        // given
        long id = 4;

        // when
        // then
        Assertions.assertThrows(DeletedEntityException.class, () -> {
            freeBoardService.getFreeBoard(id);
        });
    }

    @Test
    @DisplayName("작성자는 게시글을 변경할 수 있다.")
    void update(){
        // given
        FreeBoardRequest request = FreeBoardRequest.builder()
                .id(1L)
                .title("제목수정")
                .contents("내용수정")
                .build();
        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        FreeBoard result = freeBoardService.update(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("제목수정");
        assertThat(result.getContents()).isEqualTo("내용수정");
        assertThat(result.getCount()).isZero();
        assertThat(result.getWriterId()).isEqualTo("1111");
    }

    @Test
    @DisplayName("등록되지 않은 게시물은 수정할 수 없다.")
    void failedUpdateWhenNotFound(){
        // given
        FreeBoardRequest request = FreeBoardRequest.builder()
                .id(999L)
                .title("제목수정")
                .contents("내용수정")
                .build();

        // when
        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            freeBoardService.update(request);
        });
    }

    @Test
    @DisplayName("이미 삭제된 게시물은 수정할 수 없다.")
    void failedUpdateWhenDeleted(){
        // given
        FreeBoardRequest request = FreeBoardRequest.builder()
                .id(4L)
                .title("제목수정")
                .contents("내용수정")
                .build();

        // when
        // then
        Assertions.assertThrows(DeletedEntityException.class, () -> {
            freeBoardService.update(request);
        });
    }

    @Test
    @DisplayName("작성자가 다르면 수정할 수 없다.")
    void failedUpdateWhenNotEqualWriter(){
        // given
        FreeBoardRequest request = FreeBoardRequest.builder()
                .id(1L)
                .title("제목수정")
                .contents("내용수정")
                .build();
        Authentication authentication = new FakeAuthentication("2222", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(ForbiddenException.class, () -> {
            freeBoardService.update(request);
        });
    }

    @Test
    @DisplayName("작성자는 게시글 ID로 삭제할 수 있다.")
    void delete(){
        // given
        long id = 1;
        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        FreeBoard result = freeBoardService.delete(id);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.getContents()).isEqualTo("내용");
        assertThat(result.getWriterId()).isEqualTo("1111");
        assertThat(result.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("관리자는 게시글 ID로 삭제할 수 있다.")
    void deleteByAdmin(){
        // given
        long id = 1;
        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        FreeBoard result = freeBoardService.delete(id);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.getContents()).isEqualTo("내용");
        assertThat(result.getWriterId()).isEqualTo("1111");
        assertThat(result.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("등록되지 않은 게시물은 삭제할 수 없다.")
    void failedDelteWhenNotFound(){
        // given
        long id = 999;
        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(NotFoundException.class, () -> {
            freeBoardService.delete(id);
        });
    }

    @Test
    @DisplayName("이미 삭제된 게시물은 삭제할 수 없다.")
    void failedDeleteWhenDeleted(){
        // given
        long id = 4;
        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(DeletedEntityException.class, () -> {
            freeBoardService.delete(id);
        });
    }

    @Test
    @DisplayName("작성자가 다르면 삭제할 수 없다.")
    void failedDeletedWhenNotEqualWriter(){
        // given
        long id = 1;
        Authentication authentication = new FakeAuthentication("2222", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        // then
        Assertions.assertThrows(ForbiddenException.class, () -> {
            freeBoardService.delete(id);
        });
    }

    @Test
    @DisplayName("작성자 ID로 작성자가 등록한 게시글 목록을 불러올 수 있다.")
    void getFreeBoardListByWriterId(){
        // given
        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        List<FreeBoard> result = freeBoardService.getFreeBoardsByUserId("1111");

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getTitle()).isEqualTo("제목");
        assertThat(result.get(1).getTitle()).isEqualTo("제목2");
    }

    @Test
    @DisplayName("게시글 ID로 존재유무를 확인할 수 있다.")
    void existBoard(){
        // given
        Authentication authentication = new FakeAuthentication("1111", "test", Authority.ROLE_USER).getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        boolean result = freeBoardService.existBoard(1L);
        boolean result2 = freeBoardService.existBoard(99L);

        // then
        assertThat(result).isTrue();
        assertThat(result2).isFalse();
    }
}