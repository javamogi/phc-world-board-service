package com.phcworld.phcworldboardservice.messagequeue.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phcworld.phcworldboardservice.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    List<Field> fields = Arrays.asList(
            new Field("int32", false, "count"),
            new Field("int32", false, "count_of_answer"),
            new Field("int8", true, "is_deleted"),
            new Field("int64", true, "create_date"){public String name="org.apache.kafka.connect.data.Timestamp"; public int version = 1;},
            new Field("int64", true, "update_date"){public String name="org.apache.kafka.connect.data.Timestamp"; public int version = 1;},
            new Field("string", true, "title"),
            new Field("string", true, "contents"),
            new Field("string", true, "writer_id"));
    Schema schema = Schema.builder()
            .type("struct")
            .fields(fields)
            .optional(false)
            .name("boards")
            .build();

    public FreeBoard send(String topic, FreeBoard board){
        Payload payload = Payload.builder()
                .writer_id(board.getWriterId())
                .count(board.getCount())
                .count_of_answer(board.getCountOfAnswer())
                .is_deleted(0)
                .title(board.getTitle())
                .contents(board.getContents())
                .create_date(System.currentTimeMillis())
                .update_date(System.currentTimeMillis())
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
