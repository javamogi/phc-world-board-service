package com.phcworld.phcworldboardservice.freeboard.service;

import com.phcworld.phcworldboardservice.controller.port.FreeBoardResponse;
import com.phcworld.phcworldboardservice.controller.port.FreeBoardSearchDto;
import com.phcworld.phcworldboardservice.controller.port.SuccessResponse;
import com.phcworld.phcworldboardservice.domain.port.FreeBoardRequestDto;
import com.phcworld.phcworldboardservice.exception.model.NotFoundException;
import com.phcworld.phcworldboardservice.exception.model.UnauthorizedException;
import com.phcworld.phcworldboardservice.service.FreeBoardService;
import com.phcworld.phcworldboardservice.service.port.UserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FreeBoardEntityServiceTest {

    @Mock
    private FreeBoardService freeBoardService;

    private static UserResponse user;

    private static String boardId;

    @BeforeAll
    static void 회원_초기화(){
        user = UserResponse.builder()
                .email("test@test.test")
                .name("테스트")
                .profileImage("blank-profile-picture.png")
                .createDate("방금전")
                .userId("a2240b59-47f6-4ad4-ba07-f7c495909f40")
                .build();
        boardId = UUID.randomUUID().toString();
    }

    @Test
    void 게시글_등록() {
        FreeBoardRequestDto request = FreeBoardRequestDto.builder()
                .title("title")
                .contents("contents")
                .build();

        FreeBoardResponse response = FreeBoardResponse.builder()
                .title(request.title())
                .contents(request.contents())
                .writer(user)
                .boardId(boardId)
                .build();
        String token = "token";

        when(freeBoardService.registerFreeBoard(request,token)).thenReturn(response);
        FreeBoardResponse successResponse = freeBoardService.registerFreeBoard(request, token);
        assertThat(response).isEqualTo(successResponse);
    }

    @Test
    void 게시글_목록_조회(){
        FreeBoardSearchDto searchDto = FreeBoardSearchDto.builder()
                .pageNum(1)
                .pageSize(10)
                .searchType(0)
                .keyword("test")
                .build();

        FreeBoardResponse freeBoardResponse = FreeBoardResponse.builder()
                .title("테스트")
                .contents("테스트 내용")
                .writer(user)
                .boardId(boardId)
                .build();

        String boardId2 = UUID.randomUUID().toString();
        FreeBoardResponse freeBoardResponse2 = FreeBoardResponse.builder()
                .title("테스트2")
                .contents("테스트 내용2")
                .writer(user)
                .boardId(boardId2)
                .build();
        List<FreeBoardResponse> list = new ArrayList<>();
        list.add(freeBoardResponse);
        list.add(freeBoardResponse2);

        String token = "token";

        when(freeBoardService.getSearchList(searchDto, token)).thenReturn(list);
        List<FreeBoardResponse> result = freeBoardService.getSearchList(searchDto, token);
        assertThat(result).contains(freeBoardResponse)
                .contains(freeBoardResponse2);
    }

    @Test
    void 게시글_하나_가져오기(){
        FreeBoardResponse responseDto = FreeBoardResponse.builder()
                .boardId(boardId)
                .writer(user)
                .title("title")
                .contents("contents")
                .createDate("방금전")
                .count(1)
                .isNew(true)
                .isDeleteAuthority(false)
                .isModifyAuthority(false)
                .build();

        String token = "token";

        when(freeBoardService.getFreeBoard(boardId, token)).thenReturn(responseDto);
        FreeBoardResponse result = freeBoardService.getFreeBoard(boardId, token);
        assertThat(result).isEqualTo(responseDto);
    }

    @Test
    void 게시글_하나_가져오기_없음(){
        String token = "token";
        when(freeBoardService.getFreeBoard(boardId, token)).thenThrow(NotFoundException.class);
        Assertions.assertThrows(NotFoundException.class, () -> {
            freeBoardService.getFreeBoard(boardId, token);
        });
    }

    @Test
    void 게시글_수정(){
        FreeBoardRequestDto request = FreeBoardRequestDto.builder()
                .id("1111")
                .title("title")
                .contents("contents")
                .build();
        FreeBoardResponse responseDto = FreeBoardResponse.builder()
                .boardId(boardId)
                .writer(user)
                .title("title")
                .contents("contents")
                .createDate("방금전")
                .count(1)
                .isNew(true)
                .build();

        String token = "token";

        when(freeBoardService.updateFreeBoard(request, token)).thenReturn(responseDto);
        FreeBoardResponse freeBoardResponse = freeBoardService.updateFreeBoard(request, token);
        assertThat(responseDto).isEqualTo(freeBoardResponse);
    }

    @Test
    void 게시글_수정_게시글_없음(){
        FreeBoardRequestDto request = FreeBoardRequestDto.builder()
                .id("1111")
                .title("title")
                .contents("contents")
                .build();

        String token = "token";
        when(freeBoardService.updateFreeBoard(request, token)).thenThrow(NotFoundException.class);
        Assertions.assertThrows(NotFoundException.class, () -> {
            freeBoardService.updateFreeBoard(request, token);
        });
    }

    @Test
    void 게시글_삭제(){
        SuccessResponse responseDto = SuccessResponse.builder()
                .statusCode(200)
                .message("삭제 성공")
                .build();

        when(freeBoardService.deleteFreeBoard(boardId)).thenReturn(responseDto);
        SuccessResponse response = freeBoardService.deleteFreeBoard(boardId);
        assertThat(responseDto).isEqualTo(response);
    }

    @Test
    void 게시글_삭제_실패_게시글_없음(){
        String tmpId = UUID.randomUUID().toString();
        when(freeBoardService.deleteFreeBoard(tmpId)).thenThrow(NotFoundException.class);
        Assertions.assertThrows(NotFoundException.class, () -> {
            freeBoardService.deleteFreeBoard(tmpId);
        });
    }

    @Test
    void 게시글_삭제_실패_권한_없음(){
        when(freeBoardService.deleteFreeBoard(boardId)).thenThrow(UnauthorizedException.class);
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            freeBoardService.deleteFreeBoard(boardId);
        });
    }

    @Test
    void 게시글_존재_확인(){
        when(freeBoardService.existFreeBoard(boardId)).thenReturn(false);
        boolean result = freeBoardService.existFreeBoard(boardId);
        assertThat(result).isFalse();
    }
}