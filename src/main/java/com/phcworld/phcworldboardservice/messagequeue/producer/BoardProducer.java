package com.phcworld.phcworldboardservice.messagequeue.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phcworld.phcworldboardservice.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    List<Field> fields = Arrays.asList(
            new Field("string", false, "board_id"),
            new Field("int8", false, "is_deleted"),
            new Field("string", false, "update_date"),
            new Field("string", true, "title"),
            new Field("string", true, "contents"),
            new Field("string", false, "writer_id"));
    Schema schema = Schema.builder()
            .type("struct")
            .fields(fields)
            .optional(false)
            .name("boards")
            .build();

    public FreeBoard send(String topic, FreeBoard board){
        Payload payload = Payload.builder()
                .board_id(board.getBoardId())
                .writer_id(board.getWriterId())
                .is_deleted((byte)(Boolean.TRUE.equals(board.getIsDeleted()) ? 1 : 0))
                .title(board.getTitle())
                .contents(board.getContents())
                .update_date(LocalDateTime.now().withNano(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")))
                .build();

        KafkaBoardDto kafkaBoardDto = KafkaBoardDto.builder()
                .schema(schema)
                .payload(payload)
                .build();

        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(kafkaBoardDto);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("Board Producer sent data from the Board microservice: {}", kafkaBoardDto);

        return board;
    }
}
