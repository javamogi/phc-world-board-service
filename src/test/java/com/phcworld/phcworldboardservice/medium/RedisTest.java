package com.phcworld.phcworldboardservice.medium;

import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.infrastructure.Authority;
import com.phcworld.phcworldboardservice.infrastructure.UserRedisEntity;
import com.phcworld.phcworldboardservice.infrastructure.dto.FreeBoardSearch;
import com.phcworld.phcworldboardservice.service.port.FreeBoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
@ActiveProfiles("dev")
@Disabled
class RedisTest {

    @Autowired
    @Qualifier("redisBoardRepository")
    FreeBoardRepository boardRepository;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    void end(){
        redisTemplate.delete("BOARDS");
        redisTemplate.delete("BOARD_SORERD_SET");
        redisTemplate.delete("WRITER:1111");
    }

    @BeforeEach
    void init(){
        UserRedisEntity user = UserRedisEntity.builder()
                .userId("1111")
                .name("테스트")
                .email("test@test.test")
                .password("test")
                .isDelete(false)
                .authority(Authority.ROLE_USER)
                .createDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss.SSSSSS")))
                .profileImage("blank.jpg")
                .build();
        redisTemplate.opsForHash().put("USERS", user.getUserId(), user);
        FreeBoard freeBoard = FreeBoard.builder()
                .id(1L)
                .boardId("board-1")
                .title("제목")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId("1111")
                .createDate(LocalDateTime.now())
                .isDeleted(false)
                .build();
        boardRepository.save(freeBoard);
    }

    @Test
    @DisplayName("Board 도메인으로 게시글을 등록할 수 있다.")
    void register() {
        FreeBoard freeBoard = FreeBoard.builder()
                .id(2L)
                .boardId("board-2")
                .title("제목2")
                .contents("내용")
                .countOfAnswer(0)
                .count(0)
                .writerId("1111")
                .createDate(LocalDateTime.now())
                .isDeleted(false)
                .build();
        boardRepository.save(freeBoard);

        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();
        Optional<FreeBoard> findBoard = boardRepository.findByBoardId("board-2");
        queryStopWatch.stop();
        assertThat(findBoard).isPresent();
        assertThat(findBoard.get().getBoardId()).isEqualTo("board-2");
        assertThat(findBoard.get().getTitle()).isEqualTo("제목2");
        assertThat(findBoard.get().getContents()).isEqualTo("내용");
        assertThat(findBoard.get().getCount()).isZero();
        assertThat(findBoard.get().getCountOfAnswer()).isZero();
        assertThat(findBoard.get().getWriterId()).isEqualTo("1111");
        assertThat(findBoard.get().isDeleted()).isFalse();

        queryStopWatch.start();
        List<FreeBoard> boards2 = boardRepository.findByWriterId("1111");
        queryStopWatch.stop();
        log.info("findByWriterId 조회 시간 : {}", queryStopWatch.getTotalTimeSeconds());
        boards2.forEach(
                f -> log.info("board : {}", f)
        );
    }

    @Test
    @DisplayName("작성자 ID로 작성자가 등록한 게시글 목록을 가져올 수 있다.")
    void getBoardsByWriterId(){
        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();
        List<FreeBoard> findBoards = boardRepository.findByWriterId("1111");
        queryStopWatch.stop();
        assertThat(findBoards).hasSize(1);
    }

    @Test
    @DisplayName("지정한 페이지 번호와 목록 사이즈로 사이즈만큼 목록을 불러올 수 있다.")
    void getBoardsByPage(){
        for (int i = 2; i < 12; i++){
            FreeBoard temp = FreeBoard.builder()
                    .id((long) i)
                    .boardId("board-" + i)
                    .title("제목" + i)
                    .contents("내용" + i)
                    .countOfAnswer(0)
                    .count(0)
                    .writerId("1111")
                    .createDate(LocalDateTime.now())
                    .isDeleted(false)
                    .build();
            boardRepository.save(temp);
        }
        FreeBoardSearch search = FreeBoardSearch.builder()
                .pageSize(10)
                .pageNum(1)
                .build();
        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();
        List<FreeBoard> findBoards = boardRepository.findByKeyword(search);
        queryStopWatch.stop();
        assertThat(findBoards).hasSize(10);
    }

    @Test
    @DisplayName("게시글 UUID ID로 게시글을 가져올 수 있다.")
    void getBoardsByBoardId(){
        StopWatch queryStopWatch = new StopWatch();
        queryStopWatch.start();
        Optional<FreeBoard> findBoard = boardRepository.findByBoardId("board-1");
        queryStopWatch.stop();
        assertThat(findBoard).isPresent();
        assertThat(findBoard.get().getBoardId()).isEqualTo("board-1");
        assertThat(findBoard.get().getTitle()).isEqualTo("제목");
        assertThat(findBoard.get().getContents()).isEqualTo("내용");
        assertThat(findBoard.get().getCount()).isZero();
        assertThat(findBoard.get().getCountOfAnswer()).isZero();
        assertThat(findBoard.get().getWriterId()).isEqualTo("1111");
        assertThat(findBoard.get().isDeleted()).isFalse();
    }
}

