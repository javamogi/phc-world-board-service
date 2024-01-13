package com.phcworld.phcworldboardservice.web;

import com.phcworld.phcworldboardservice.dto.SuccessResponseDto;
import com.phcworld.phcworldboardservice.dto.FreeBoardRequestDto;
import com.phcworld.phcworldboardservice.dto.FreeBoardResponseDto;
import com.phcworld.phcworldboardservice.dto.FreeBoardSearchDto;
import com.phcworld.phcworldboardservice.service.FreeBoardService;
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

    @GetMapping("/{freeBoardId}")
    public ResponseEntity<Map<String, Object>> getFreeBoard(@PathVariable(name = "freeBoardId") String freeBoardId,
                                                            HttpServletRequest request){
        String token = request.getHeader("Authorization");
        Map<String, Object> result = freeBoardService.getFreeBoard(freeBoardId, token);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PatchMapping("")
    public FreeBoardResponseDto updateBoard(@RequestBody FreeBoardRequestDto requestDto,
                                            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return freeBoardService.updateFreeBoard(requestDto, token);
    }

    @DeleteMapping("/{boardId}")
    public SuccessResponseDto deleteBoard(@PathVariable(name = "boardId") String boardId){
        return freeBoardService.deleteFreeBoard(boardId);
    }

    @GetMapping("/{boardId}/exist")
    public boolean getExist(@PathVariable(name = "boardId") String boardId){
        return freeBoardService.existFreeBoard(boardId);
    }
}
