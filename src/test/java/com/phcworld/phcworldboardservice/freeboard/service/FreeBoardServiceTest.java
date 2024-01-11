package com.phcworld.phcworldboardservice.freeboard.service;

import com.phcworld.phcworldboardservice.dto.*;
import com.phcworld.phcworldboardservice.exception.model.NotFoundException;
import com.phcworld.phcworldboardservice.exception.model.UnauthorizedException;
import com.phcworld.phcworldboardservice.service.FreeBoardService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FreeBoardServiceTest {

    @Mock
    private FreeBoardService freeBoardService;

    private static UserResponseDto user;

    @BeforeAll
    static void 회원_초기화(){
        user = UserResponseDto.builder()
                .id(1L)
                .email("test@test.test")
                .name("테스트")
                .profileImage("blank-profile-picture.png")
                .createDate("방금전")
                .userId("a2240b59-47f6-4ad4-ba07-f7c495909f40")
                .build();
    }

    @Test
    void 게시글_등록() {
        FreeBoardRequestDto request = FreeBoardRequestDto.builder()
                .title("title")
                .contents("contents")
                .build();

        FreeBoardResponseDto response = FreeBoardResponseDto.builder()
                .id(1L)
                .title(request.title())
                .contents(request.contents())
                .writer(user)
                .build();
        String token = "token";

        when(freeBoardService.registerFreeBoard(request,token)).thenReturn(response);
        FreeBoardResponseDto successResponse = freeBoardService.registerFreeBoard(request, token);
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

        FreeBoardResponseDto freeBoardResponse = FreeBoardResponseDto.builder()
                .id(1L)
                .title("테스트")
                .contents("테스트 내용")
                .writer(user)
                .build();

        FreeBoardResponseDto freeBoardResponse2 = FreeBoardResponseDto.builder()
                .id(2L)
                .title("테스트2")
                .contents("테스트 내용2")
                .writer(user)
                .build();
        List<FreeBoardResponseDto> list = new ArrayList<>();
        list.add(freeBoardResponse);
        list.add(freeBoardResponse2);

        String token = "token";

        when(freeBoardService.getSearchList(searchDto, token)).thenReturn(list);
        List<FreeBoardResponseDto> result = freeBoardService.getSearchList(searchDto, token);
        assertThat(result).contains(freeBoardResponse)
                .contains(freeBoardResponse2);
    }

    @Test
    void 게시글_하나_가져오기(){
        Map<String, Object> map = new HashMap<>();
        FreeBoardResponseDto responseDto = FreeBoardResponseDto.builder()
                .id(1L)
                .writer(user)
                .title("title")
                .contents("contents")
                .createDate("방금전")
                .count(1)
                .isNew(true)
                .build();
        map.put("freeboard", responseDto);
        map.put("isDeletedAuthrity", false);
        map.put("isModifyAuthrity", false);

        String token = "token";

        when(freeBoardService.getFreeBoard(1L, token)).thenReturn(map);
        Map<String, Object> result = freeBoardService.getFreeBoard(1L, token);
        assertThat(result).isEqualTo(map);
    }

    @Test
    void 게시글_하나_가져오기_없음(){
        String token = "token";
        when(freeBoardService.getFreeBoard(1L, token)).thenThrow(NotFoundException.class);
        Assertions.assertThrows(NotFoundException.class, () -> {
            freeBoardService.getFreeBoard(1L, token);
        });
    }

    @Test
    void 게시글_수정(){
        FreeBoardRequestDto request = FreeBoardRequestDto.builder()
                .id(1L)
                .title("title")
                .contents("contents")
                .build();
        FreeBoardResponseDto responseDto = FreeBoardResponseDto.builder()
                .id(1L)
                .writer(user)
                .title("title")
                .contents("contents")
                .createDate("방금전")
                .count(1)
                .isNew(true)
                .build();

        String token = "token";

        when(freeBoardService.updateFreeBoard(request, token)).thenReturn(responseDto);
        FreeBoardResponseDto freeBoardResponse = freeBoardService.updateFreeBoard(request, token);
        assertThat(responseDto).isEqualTo(freeBoardResponse);
    }

    @Test
    void 게시글_수정_게시글_없음(){
        FreeBoardRequestDto request = FreeBoardRequestDto.builder()
                .id(1L)
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
        SuccessResponseDto responseDto = SuccessResponseDto.builder()
                .statusCode(200)
                .message("삭제 성공")
                .build();

        when(freeBoardService.deleteFreeBoard(1L)).thenReturn(responseDto);
        SuccessResponseDto response = freeBoardService.deleteFreeBoard(1L);
        assertThat(responseDto).isEqualTo(response);
    }

    @Test
    void 게시글_삭제_실패_게시글_없음(){
        when(freeBoardService.deleteFreeBoard(100L)).thenThrow(NotFoundException.class);
        Assertions.assertThrows(NotFoundException.class, () -> {
            freeBoardService.deleteFreeBoard(100L);
        });
    }

    @Test
    void 게시글_삭제_실패_권한_없음(){
        when(freeBoardService.deleteFreeBoard(1L)).thenThrow(UnauthorizedException.class);
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            freeBoardService.deleteFreeBoard(1L);
        });
    }

    @Test
    void 게시글_존재_확인(){
        when(freeBoardService.existFreeBoard(1L)).thenReturn(false);
        boolean result = freeBoardService.existFreeBoard(1L);
        assertThat(result).isFalse();
    }
}