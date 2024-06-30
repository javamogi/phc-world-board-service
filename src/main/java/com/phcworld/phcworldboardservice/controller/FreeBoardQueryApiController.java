package com.phcworld.phcworldboardservice.controller;

import com.phcworld.phcworldboardservice.controller.port.FreeBoardService;
import com.phcworld.phcworldboardservice.controller.port.WebclientService;
import com.phcworld.phcworldboardservice.infrastructure.dto.FreeBoardSearch;
import com.phcworld.phcworldboardservice.controller.response.FreeBoardResponse;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.service.dto.FreeBoardAnswerResponse;
import com.phcworld.phcworldboardservice.service.dto.UserResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/freeboards")
@RequiredArgsConstructor
@Builder
public class FreeBoardQueryApiController {

    private final FreeBoardService freeBoardService;
    private final WebclientService webclientService;

    @GetMapping("")
    public ResponseEntity<List<FreeBoardResponse>> getList(FreeBoardSearch search,
                                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String token){

        if(Objects.nonNull(search.searchType()) && search.searchType().equals(3) && !search.keyword().isEmpty()){
            search = webclientService.getUserIdByName(token, search);
        }

        List<FreeBoard> freeBoards = freeBoardService.getSearchList(search);
        Map<String, UserResponse> users = webclientService.getUsers(token, freeBoards);
        List<FreeBoardResponse> result = freeBoards.stream()
                .map(f -> {
                    return FreeBoardResponse.builder()
                            .boardId(f.getBoardId())
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

    @GetMapping("/{freeBoardId}/exist")
    public ResponseEntity<FreeBoardResponse> existFreeBoard(@PathVariable(name = "freeBoardId") String freeBoardId){
        FreeBoard result = freeBoardService.existBoard(freeBoardId);
        return ResponseEntity
                .ok()
                .body(FreeBoardResponse.of(result));
    }
}
