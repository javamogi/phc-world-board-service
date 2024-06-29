package com.phcworld.phcworldboardservice.infrastructure;

import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.exception.model.NotFoundException;
import com.phcworld.phcworldboardservice.infrastructure.dto.FreeBoardSearch;
import com.phcworld.phcworldboardservice.service.port.FreeBoardRepository;
import com.phcworld.phcworldboardservice.service.port.UuidHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.*;


@Repository("redisBoardRepository")
@RequiredArgsConstructor
@Slf4j
public class BoardRedisRepositoryImpl implements FreeBoardRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UuidHolder uuidHolder;

    private final String USER_HASH_KEY = "USERS";
    private final String BOARD_HASH_KEY = "BOARDS";
    private final String BOARD_SORTED_SET_KEY = "BOARD_SORTED_SET:";
    private final String WRITER_KEY = "WRITER:";

    @Override
    public List<FreeBoard> findByWriterId(String writerId){
        Set<Object> boards = redisTemplate.opsForSet().members(WRITER_KEY + writerId);
        if(Objects.isNull(boards) || boards.isEmpty()){
            return new ArrayList<>();
        }
        return boards.stream()
                .filter(boardId -> {
                    FreeBoardRedisEntity board = (FreeBoardRedisEntity) redisTemplate.opsForHash().get(BOARD_HASH_KEY, (String) boardId);
                    return !board.isDelete();
                })
                .map(boardId -> ((FreeBoardRedisEntity) redisTemplate.opsForHash().get(BOARD_HASH_KEY, boardId)).toModel())
                .toList();
    }

    @Override
    public Optional<FreeBoard> findById(Long boardId) {
        HashOperations<String, String, Object> hops = redisTemplate.opsForHash();
        FreeBoardRedisEntity freeBoardRedisEntity = (FreeBoardRedisEntity) hops.get(BOARD_HASH_KEY, boardId);
        if(Objects.isNull(freeBoardRedisEntity)){
            return Optional.empty();
        }
        return Optional.ofNullable(freeBoardRedisEntity.toModel());
    }

    @Override
    public List<FreeBoard> findByKeyword(FreeBoardSearch searchDto){
        int start = (searchDto.pageNum() - 1) * searchDto.pageSize();
        int end = start + searchDto.pageSize() - 1;
        Set<Object> boardIds = redisTemplate.opsForZSet().reverseRange(BOARD_SORTED_SET_KEY, start, end);
        if(Objects.isNull(boardIds) || boardIds.isEmpty()){
            return new ArrayList<>();
        }
        return redisTemplate.opsForHash().multiGet(BOARD_HASH_KEY, boardIds).stream()
                .map(boardId -> ((FreeBoardRedisEntity) boardId).toModel())
                .toList();
    }

    @Override
    public FreeBoard save(FreeBoard freeBoard){

        String boardId = freeBoard.getBoardId();
        String writerId = freeBoard.getWriterId();
        Object user = redisTemplate.opsForHash().get(USER_HASH_KEY, writerId);
        if (user == null) {
            throw new NotFoundException();
        }

        FreeBoardRedisEntity entity = (FreeBoardRedisEntity) redisTemplate.opsForHash().get(BOARD_HASH_KEY, boardId);
        while (Objects.nonNull(entity)){
            freeBoard = FreeBoard.from(freeBoard, uuidHolder);
            boardId = freeBoard.getBoardId();
            entity = (FreeBoardRedisEntity) redisTemplate.opsForHash().get(BOARD_HASH_KEY, boardId);
        }

        FreeBoardRedisEntity board = FreeBoardRedisEntity.from(freeBoard, boardId);
        redisTemplate.opsForHash().put(BOARD_HASH_KEY, boardId, board);
        redisTemplate.opsForZSet().add(BOARD_SORTED_SET_KEY,
                boardId,
                freeBoard.getCreateDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        redisTemplate.opsForSet().add(WRITER_KEY + writerId, boardId);
        return board.toModel();
    }

    @Override
    public Optional<FreeBoard> findByBoardId(String boardId) {
        HashOperations<String, String, Object> hops = redisTemplate.opsForHash();
        FreeBoardRedisEntity freeBoardRedisEntity = (FreeBoardRedisEntity) hops.get(BOARD_HASH_KEY, boardId);
        if (Objects.isNull(freeBoardRedisEntity)) {
            return Optional.empty();
        }
        return Optional.ofNullable(freeBoardRedisEntity.toModel());
    }
}
