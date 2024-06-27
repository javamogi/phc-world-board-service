package com.phcworld.phcworldboardservice.controller;

import com.phcworld.phcworldboardservice.controller.port.FreeBoardService;
import com.phcworld.phcworldboardservice.controller.port.WebclientService;
import com.phcworld.phcworldboardservice.controller.response.FreeBoardResponse;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.domain.FreeBoardRequest;
import com.phcworld.phcworldboardservice.service.dto.UserResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/freeboards")
@RequiredArgsConstructor
@Builder
public class FreeBoardCommandApiController {


    private final FreeBoardService freeBoardService;
    private final WebclientService webclientService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<FreeBoardResponse> register(@RequestBody FreeBoardRequest requestDto,
                                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        UserResponse user = webclientService.getUser(token, null);
        FreeBoard freeBoard = freeBoardService.register(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(FreeBoardResponse.of(freeBoard, user));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "요청 게시글 없음"),
            @ApiResponse(responseCode = "409", description = "삭제된 게시글"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음")
    })
    @PatchMapping("")
    public ResponseEntity<FreeBoardResponse> update(@RequestBody FreeBoardRequest requestDto,
                                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        FreeBoard freeBoard = freeBoardService.update(requestDto);
        UserResponse user = webclientService.getUser(token, freeBoard);
        return ResponseEntity
                .ok()
                .body(FreeBoardResponse.of(freeBoard, user));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "요청 게시글 없음"),
            @ApiResponse(responseCode = "409", description = "삭제된 게시글"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음")
    })
    @DeleteMapping("/{boardId}")
    public ResponseEntity<FreeBoardResponse> delete(@PathVariable(name = "boardId") Long boardId,
                                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        FreeBoard freeBoard = freeBoardService.delete(boardId);
        UserResponse user = webclientService.getUser(token, freeBoard);
        return ResponseEntity
                .ok()
                .body(FreeBoardResponse.of(freeBoard, user));
    }

}
