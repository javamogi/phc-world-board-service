package com.phcworld.phcworldboardservice.controller;

import com.phcworld.phcworldboardservice.controller.port.FreeBoardResponse;
import com.phcworld.phcworldboardservice.controller.port.FreeBoardSearchDto;
import com.phcworld.phcworldboardservice.controller.port.FreeBoardService;
import com.phcworld.phcworldboardservice.controller.port.WebclientService;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.domain.port.FreeBoardRequest;
import com.phcworld.phcworldboardservice.service.port.FreeBoardAnswerResponse;
import com.phcworld.phcworldboardservice.service.port.UserResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
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
public class FreeBoardApiController {


    private final FreeBoardService freeBoardService;
    private final WebclientService webclientService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<FreeBoardResponse> registerBoard(@RequestBody FreeBoardRequest requestDto, HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        FreeBoard freeBoard = freeBoardService.register(requestDto);
        UserResponse user = webclientService.getUser(token, freeBoard);
        return ResponseEntity
                .ok()
                .body(FreeBoardResponse.of(user, freeBoard));
    }

    @GetMapping("")
    public ResponseEntity<List<FreeBoardResponse>> getList(FreeBoardSearchDto search, HttpServletRequest request){
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        List<FreeBoard> freeBoards = freeBoardService.getSearchList(search);
        Map<String, UserResponse> users = webclientService.getUsers(token, freeBoards);
        List<FreeBoardResponse> result = freeBoards.stream()
				.map(f -> {
					return FreeBoardResponse.builder()
							.boardId(f.getId())
							.title(f.getTitle())
							.contents(f.getContents())
							.writer(users != null ? users.get(f.getWriterId()) : null)
							.count(f.getCount())
							.countOfAnswer(f.getCountOfAnswer())
							.build();
				})
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
                                          HttpServletRequest request){
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        FreeBoard freeBoard = freeBoardService.getFreeBoard(freeBoardId);
        UserResponse user = webclientService.getUser(token, freeBoard);
        List<FreeBoardAnswerResponse> answers = webclientService.getAnswers(token, freeBoard);
        return ResponseEntity
                .ok()
                .body(FreeBoardResponse.of(user, freeBoard, answers));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "요청 게시글 없음"),
            @ApiResponse(responseCode = "409", description = "삭제된 게시글"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음")
    })
    @PatchMapping("")
    public ResponseEntity<FreeBoardResponse> updateBoard(@RequestBody FreeBoardRequest requestDto,
                                         HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        FreeBoard freeBoard = freeBoardService.update(requestDto);
        UserResponse user = webclientService.getUser(token, freeBoard);
        return ResponseEntity
                .ok()
                .body(FreeBoardResponse.of(user, freeBoard));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "요청 게시글 없음"),
            @ApiResponse(responseCode = "409", description = "삭제된 게시글"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음")
    })
    @DeleteMapping("/{boardId}")
    public ResponseEntity<FreeBoardResponse> deleteBoard(@PathVariable(name = "boardId") Long boardId,
                                                         HttpServletRequest request){
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        FreeBoard freeBoard = freeBoardService.delete(boardId);
        UserResponse user = webclientService.getUser(token, freeBoard);
        return ResponseEntity
                .ok()
                .body(FreeBoardResponse.of(user, freeBoard));
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
