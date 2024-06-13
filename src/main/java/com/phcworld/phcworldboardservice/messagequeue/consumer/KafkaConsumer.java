package com.phcworld.phcworldboardservice.messagequeue.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phcworld.phcworldboardservice.infrastructure.FreeBoardEntity;
import com.phcworld.phcworldboardservice.exception.model.NotFoundException;
import com.phcworld.phcworldboardservice.infrastructure.FreeBoardJpaJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final FreeBoardJpaJpaRepository repository;
    private final ObjectMapper mapper;

    @KafkaListener(topics = "board-topic")
    @Transactional
    public void updateCountOfAnswer(String kafkaMessage){
        log.info("kafka message : -> {}", kafkaMessage);

        Map<Object, Object> map = new HashMap<>();
        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }

        String boardId = (String) map.get("freeBoardId");
        FreeBoardEntity entity = repository.findByBoardId(boardId)
                .orElseThrow(NotFoundException::new);
        entity.addCountOfAnswer();
    }
}
