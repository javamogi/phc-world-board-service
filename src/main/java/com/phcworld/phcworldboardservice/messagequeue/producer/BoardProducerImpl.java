package com.phcworld.phcworldboardservice.messagequeue.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.exception.model.InternalServerErrorException;
import com.phcworld.phcworldboardservice.messagequeue.port.*;
import com.phcworld.phcworldboardservice.service.port.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardProducerImpl implements KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    @Override
    public FreeBoard send(String topic, FreeBoard board, boolean isUpdate){
        Payload payload = Payload.builder()
                .id(board.getId())
                .writer_id(board.getWriter().getUserId())
                .is_deleted((byte)(Boolean.TRUE.equals(board.isDeleted()) ? 1 : 0))
                .title(board.getTitle())
                .contents(board.getContents())
                .update_date(board.getUpdateDate().withNano(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")))
                .count(board.getCount())
                .count_of_answer(board.getCountOfAnswer())
                .build();
        List<Field> fields;
        if(isUpdate){
            fields = Arrays.asList(
                    new Field("int64", false, "id"),
                    new Field("int8", false, "is_deleted"),
                    new Field("int32", true, "count"),
                    new Field("int32", true, "count_of_answer"),
                    new Field("string", false, "update_date"),
                    new Field("string", true, "title"),
                    new Field("string", true, "contents"),
                    new Field("string", false, "writer_id"));
        } else {
            fields = Arrays.asList(
                    new Field("int8", false, "is_deleted"),
                    new Field("string", false, "update_date"),
                    new Field("string", true, "title"),
                    new Field("string", true, "contents"),
                    new Field("string", false, "writer_id"));
        }

        Schema schema = Schema.builder()
                .type("struct")
                .fields(fields)
                .optional(false)
                .name("boards")
                .build();

        KafkaBoardDto kafkaBoardDto = KafkaBoardDto.builder()
                .schema(schema)
                .payload(payload)
                .build();

        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(kafkaBoardDto);
        } catch (JsonProcessingException e){
            throw new InternalServerErrorException();
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("Board Producer sent data from the Board microservice: {}", kafkaBoardDto);

        return board;
    }
}
