package com.phcworld.phcworldboardservice.web;

import com.phcworld.phcworldboardservice.dto.SuccessResponseDto;
import com.phcworld.phcworldboardservice.dto.FreeBoardRequestDto;
import com.phcworld.phcworldboardservice.dto.FreeBoardResponseDto;
import com.phcworld.phcworldboardservice.dto.FreeBoardSearchDto;
import com.phcworld.phcworldboardservice.service.FreeBoardService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/freeboards")
@RequiredArgsConstructor
public class FreeBoardApiController {

    private final FreeBoardService freeBoardService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "DB에 저장할 때 UNIQUE 충돌")
    })
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public FreeBoardResponseDto registerBoard(@RequestBody FreeBoardRequestDto requestDto, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return freeBoardService.registerFreeBoard(requestDto, token);
    }

    @GetMapping("")
    public List<FreeBoardResponseDto> getList(FreeBoardSearchDto search, HttpServletRequest request){
        String token = request.getHeader("Authorization");
        return freeBoardService.getSearchList(search, token);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "요청 게시글 없음"),
            @ApiResponse(responseCode = "409", description = "삭제된 게시글")
    })
    @GetMapping("/{freeBoardId}")
    public ResponseEntity<Map<String, Object>> getFreeBoard(@PathVariable(name = "freeBoardId") String freeBoardId,
                                                            HttpServletRequest request){
        String token = request.getHeader("Authorization");
        Map<String, Object> result = freeBoardService.getFreeBoard(freeBoardId, token);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "요청 게시글 없음"),
            @ApiResponse(responseCode = "409", description = "삭제된 게시글"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음")
    })
    @PatchMapping("")
    public FreeBoardResponseDto updateBoard(@RequestBody FreeBoardRequestDto requestDto,
                                            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return freeBoardService.updateFreeBoard(requestDto, token);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "요청 게시글 없음"),
            @ApiResponse(responseCode = "409", description = "삭제된 게시글"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음")
    })
    @DeleteMapping("/{boardId}")
    public SuccessResponseDto deleteBoard(@PathVariable(name = "boardId") String boardId){
        return freeBoardService.deleteFreeBoard(boardId);
    }

    @GetMapping("/{boardId}/exist")
    public boolean getExist(@PathVariable(name = "boardId") String boardId){
        return freeBoardService.existFreeBoard(boardId);
    }
}
