package com.phcworld.phcworldboardservice.freeboard.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phcworld.phcworldboardservice.domain.Authority;
import com.phcworld.phcworldboardservice.dto.FreeBoardRequestDto;
import com.phcworld.phcworldboardservice.jwt.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Disabled("Disabled when build")
class FreeBoardApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @SpyBean
    private TokenProvider tokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private static String token;

    @BeforeEach
    void 토큰_생성(){
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{Authority.ROLE_ADMIN.toString()})
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new org.springframework.security.core.userdetails.User("a2240b59-47f6-4ad4-ba07-f7c495909f40", "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        long now = (new Date()).getTime();
        String accessToken = tokenProvider.generateAccessToken(authentication, now);
        token = "Bearer " + accessToken;
    }

    @Disabled("kafka 서버가 기동 되었을 때만 가능")
    @Test
    void 게시글_등록_성공() throws Exception {
        FreeBoardRequestDto requestDto = FreeBoardRequestDto.builder()
                .title("title")
                .contents("contents")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);

        this.mvc.perform(post("/freeboards")
                        .header("Authorization", token)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Disabled("kafka 서버가 기동 되었을 때만 가능")
    @Test
    void 게시글_등록_성공_이미지_첨부() throws Exception {
//        String contents = FileConvertUtils.getFileData("blank-profile-picture.png");
//        contents = "<p><img src=\"" + contents + "\"></p>";
//        String contents2 = FileConvertUtils.getFileData("PHC-WORLD.png");
//        contents2 = "<p><img src=\"" + contents2 + "\"></p>";
        FreeBoardRequestDto requestDto = FreeBoardRequestDto.builder()
                .title("title")
//                .contents(contents + contents2)
                .contents("new contents")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);

        this.mvc.perform(post("/freeboards")
                        .header("Authorization", token)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void 게시글_목록_조회() throws Exception {

        this.mvc.perform(get("/freeboards")
                        .header("Authorization", token)
                        .with(csrf())
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("searchType", "0")
                        .param("keyword", ""))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 게시글_하나_조회() throws Exception {

        this.mvc.perform(get("/freeboards/{freeBoardId}", "1111")
                        .header("Authorization", token)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 게시글_하나_조회_데이터_없음() throws Exception {
        this.mvc.perform(get("/freeboards/{freeBoardId}", "9999")
                        .header("Authorization", token)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void 게시글_수정_성공() throws Exception {
//        String contents = FileConvertUtils.getFileData("blank-profile-picture.png");
//        contents = "<p><img src=\"" + contents + "\"></p>";
        FreeBoardRequestDto requestDto = FreeBoardRequestDto.builder()
                .id(1L)
                .title("제목")
//                .contents(contents)
                .contents("update contents")
                .build();

        String request = objectMapper.writeValueAsString(requestDto);
        this.mvc.perform(patch("/freeboards")
                        .header("Authorization", token)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 게시글_수정_실패_권한_없음() throws Exception {
        FreeBoardRequestDto requestDto = FreeBoardRequestDto.builder()
                .id(1L)
                .title("제목")
                .contents("내용")
                .build();
        String request = objectMapper.writeValueAsString(requestDto);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{Authority.ROLE_USER.toString()})
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new org.springframework.security.core.userdetails.User("2", "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        long now = (new Date()).getTime();
        String accessToken = "Bearer " + tokenProvider.generateAccessToken(authentication, now);

        this.mvc.perform(patch("/freeboards")
                        .header("Authorization", accessToken)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void 게시글_삭제_성공() throws Exception {
        this.mvc.perform(delete("/freeboards/{freeBoardId}", "1111")
                        .header("Authorization", token)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 게시글_삭제_실패_권한_없음() throws Exception {
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{Authority.ROLE_USER.toString()})
                        .map(SimpleGrantedAuthority::new)
                        .toList();
        UserDetails principal = new org.springframework.security.core.userdetails.User("2", "", authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        long now = (new Date()).getTime();
        String accessToken = "Bearer " + tokenProvider.generateAccessToken(authentication, now);

        this.mvc.perform(delete("/freeboards/{freeBoardId}", "1111")
                        .header("Authorization", accessToken)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

}