package com.phcworld.phcworldboardservice.messagequeue.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.exception.model.InternalServerErrorException;
import com.phcworld.phcworldboardservice.exception.model.NotFoundException;
import com.phcworld.phcworldboardservice.service.port.FreeBoardRepository;
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
public class KafkaConsumerImpl implements KafkaConsumer {
    private final FreeBoardRepository repository;
    private final ObjectMapper mapper;

//    @KafkaListener(topics = "board-topic")
    @KafkaListener(topics = "answers")
    @Transactional
    public void updateCountOfAnswer(String kafkaMessage){
        log.info("kafka message : -> {}", kafkaMessage);

        Map<Object, Object> map = new HashMap<>();
        try {
            JsonNode rootNode = mapper.readTree(kafkaMessage);
            JsonNode payload = rootNode.get("payload");
            log.info(payload.toString());
            map = mapper.convertValue(payload, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException e){
            throw new InternalServerErrorException();
        }

        Long boardId = (long) (int) map.get("free_board_id");
        FreeBoard freeBoard = repository.findById(boardId)
                .orElseThrow(NotFoundException::new);
        freeBoard = freeBoard.addCountOfAnswer();
        repository.save(freeBoard);
    }
}
