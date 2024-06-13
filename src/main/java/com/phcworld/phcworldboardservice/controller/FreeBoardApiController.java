package com.phcworld.phcworldboardservice.controller;

import com.phcworld.phcworldboardservice.controller.port.SuccessResponse;
import com.phcworld.phcworldboardservice.domain.port.FreeBoardRequestDto;
import com.phcworld.phcworldboardservice.controller.port.FreeBoardResponse;
import com.phcworld.phcworldboardservice.controller.port.FreeBoardSearchDto;
import com.phcworld.phcworldboardservice.service.FreeBoardService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public FreeBoardResponse registerBoard(@RequestBody FreeBoardRequestDto requestDto, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return freeBoardService.registerFreeBoard(requestDto, token);
    }

    @GetMapping("")
    public List<FreeBoardResponse> getList(FreeBoardSearchDto search, HttpServletRequest request){
        String token = request.getHeader("Authorization");
        return freeBoardService.getSearchList(search, token);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "요청 게시글 없음"),
            @ApiResponse(responseCode = "409", description = "삭제된 게시글")
    })
    @GetMapping("/{freeBoardId}")
    public FreeBoardResponse getFreeBoard(@PathVariable(name = "freeBoardId") String freeBoardId,
                                          HttpServletRequest request){
        String token = request.getHeader("Authorization");
        FreeBoardResponse result = freeBoardService.getFreeBoard(freeBoardId, token);
//        return new ResponseEntity<>(result, HttpStatus.OK);
        return result;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "요청 게시글 없음"),
            @ApiResponse(responseCode = "409", description = "삭제된 게시글"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음")
    })
    @PatchMapping("")
    public FreeBoardResponse updateBoard(@RequestBody FreeBoardRequestDto requestDto,
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
    public SuccessResponse deleteBoard(@PathVariable(name = "boardId") String boardId){
        return freeBoardService.deleteFreeBoard(boardId);
    }

    @GetMapping("/{boardId}/exist")
    public boolean getExist(@PathVariable(name = "boardId") String boardId){
        return freeBoardService.existFreeBoard(boardId);
    }
}
