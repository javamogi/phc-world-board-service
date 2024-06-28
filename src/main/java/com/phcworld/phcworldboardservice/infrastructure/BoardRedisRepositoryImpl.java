package com.phcworld.phcworldboardservice.infrastructure;

import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.exception.model.NotFoundException;
import com.phcworld.phcworldboardservice.infrastructure.dto.FreeBoardSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.*;


@Repository
@RequiredArgsConstructor
@Slf4j
public class BoardRedisRepositoryImpl {

    private final RedisTemplate<String, Object> redisTemplate;

    private final String USER_HASH_KEY = "USERS";
    private final String BOARD_HASH_KEY = "BOARDS";
    private final String BOARD_SORTED_SET_KEY = "BOARD_SORERD_SET:";
    private final String WRITER_KEY = "WRITER:";


    public List<FreeBoard> findByWriterId(String writerId){
        Set<Object> boards = redisTemplate.opsForSet().members(writerId);
        if(Objects.isNull(boards) || boards.isEmpty()){
            return new ArrayList<>();
        }
        return boards.stream()
                .map(f -> ((FreeBoardRedisEntity) f).toModel())
                .toList();
    }
    public Optional<FreeBoard> findById(Long boardId) {
        HashOperations<String, String, Object> hops = redisTemplate.opsForHash();
        FreeBoardRedisEntity freeBoardRedisEntity = (FreeBoardRedisEntity) hops.get(BOARD_HASH_KEY, boardId);
        if(Objects.isNull(freeBoardRedisEntity)){
            return Optional.empty();
        }
        return Optional.ofNullable(freeBoardRedisEntity.toModel());
    }
    public List<FreeBoard> findByKeyword(FreeBoardSearch searchDto, Pageable pageable){
        return null;
    }
    public FreeBoard save(FreeBoard freeBoard){
        String id = UUID.randomUUID().toString();
        Object user = redisTemplate.opsForHash().get(USER_HASH_KEY, id);
        if (user == null) {
            throw new NotFoundException();
        }
        FreeBoardRedisEntity board = FreeBoardRedisEntity.from(freeBoard, id);
        redisTemplate.opsForHash().put(BOARD_HASH_KEY, id, board);
        redisTemplate.opsForZSet().add(BOARD_SORTED_SET_KEY,
                freeBoard.getId(),
                freeBoard.getCreateDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        redisTemplate.opsForSet().add(WRITER_KEY + board.getWriterId(), id);
        return board.toModel();
    }

}
