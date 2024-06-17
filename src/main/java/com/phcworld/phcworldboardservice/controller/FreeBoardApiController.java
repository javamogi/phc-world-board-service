package com.phcworld.phcworldboardservice.controller;

import com.phcworld.phcworldboardservice.controller.port.FreeBoardResponse;
import com.phcworld.phcworldboardservice.controller.port.FreeBoardSearch;
import com.phcworld.phcworldboardservice.controller.port.FreeBoardService;
import com.phcworld.phcworldboardservice.controller.port.WebclientService;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.domain.port.FreeBoardRequest;
import com.phcworld.phcworldboardservice.service.port.FreeBoardAnswerResponse;
import com.phcworld.phcworldboardservice.service.port.UserResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/freeboards")
@RequiredArgsConstructor
@Builder
public class FreeBoardApiController {


    private final FreeBoardService freeBoardService;
    private final WebclientService webclientService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<FreeBoardResponse> registerBoard(@RequestBody FreeBoardRequest requestDto,
                                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        FreeBoard freeBoard = freeBoardService.register(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(FreeBoardResponse.of(freeBoard));
    }

    @GetMapping("")
    public ResponseEntity<List<FreeBoardResponse>> getList(FreeBoardSearch search,
                                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        List<FreeBoard> freeBoards = freeBoardService.getSearchList(search);
        List<FreeBoardResponse> result = freeBoards.stream()
				.map(FreeBoardResponse::of)
				.toList();
        return ResponseEntity
                .ok()
                .body(result);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "요청 게시글 없음"),
            @ApiResponse(responseCode = "409", description = "삭제된 게시글")
    })
    @GetMapping("/{freeBoardId}")
    public ResponseEntity<FreeBoardResponse> getFreeBoard(@PathVariable(name = "freeBoardId") Long freeBoardId,
                                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        FreeBoard freeBoard = freeBoardService.getFreeBoard(freeBoardId);
        List<FreeBoardAnswerResponse> answers = webclientService.getAnswers(token, freeBoard);
        return ResponseEntity
                .ok()
                .body(FreeBoardResponse.of(freeBoard, answers));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "요청 게시글 없음"),
            @ApiResponse(responseCode = "409", description = "삭제된 게시글"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음")
    })
    @PatchMapping("")
    public ResponseEntity<FreeBoardResponse> updateBoard(@RequestBody FreeBoardRequest requestDto,
                                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        FreeBoard freeBoard = freeBoardService.update(requestDto);
        return ResponseEntity
                .ok()
                .body(FreeBoardResponse.of(freeBoard));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "요청 게시글 없음"),
            @ApiResponse(responseCode = "409", description = "삭제된 게시글"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음")
    })
    @DeleteMapping("/{boardId}")
    public ResponseEntity<FreeBoardResponse> deleteBoard(@PathVariable(name = "boardId") Long boardId,
                                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        FreeBoard freeBoard = freeBoardService.delete(boardId);
        return ResponseEntity
                .ok()
                .body(FreeBoardResponse.of(freeBoard));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<FreeBoardResponse>> getFreeBoardsByUser(@PathVariable(name = "userId") String userId){
        List<FreeBoardResponse> result = freeBoardService.getFreeBoardsByUserId(userId)
                .stream()
                .map(FreeBoardResponse::of)
                .toList();
        return ResponseEntity
                .ok()
                .body(result);
    }

}
