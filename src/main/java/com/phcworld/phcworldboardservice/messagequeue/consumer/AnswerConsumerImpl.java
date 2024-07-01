package com.phcworld.phcworldboardservice.messagequeue.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.exception.model.InternalServerErrorException;
import com.phcworld.phcworldboardservice.exception.model.NotFoundException;
import com.phcworld.phcworldboardservice.service.port.FreeBoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class AnswerConsumerImpl {
    private final FreeBoardRepository repository;
    private final ObjectMapper mapper;

    public AnswerConsumerImpl(@Qualifier("jpaBoardRepository") FreeBoardRepository repository,
                              ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @KafkaListener(topics = "answers")
    @Transactional
    public void updateCountOfAnswer(String kafkaMessage){
        log.info("kafka message : -> {}", kafkaMessage);

        Map<Object, Object> map = new HashMap<>();
        try {
            JsonNode rootNode = mapper.readTree(kafkaMessage);
            JsonNode payloadNode = rootNode.get("payload");
            map = mapper.convertValue(payloadNode, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException e){
            throw new InternalServerErrorException();
        }
        if(map.get("id") == null){
            Long boardId = (long) (int) map.get("free_board_id");
            FreeBoard freeBoard = repository.findById(boardId)
                    .orElseThrow(NotFoundException::new);
            freeBoard = freeBoard.addCountOfAnswer();
            repository.save(freeBoard);
        }
    }
}
