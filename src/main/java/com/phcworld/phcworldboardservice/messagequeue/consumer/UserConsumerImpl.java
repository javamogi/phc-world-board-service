package com.phcworld.phcworldboardservice.messagequeue.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phcworld.phcworldboardservice.domain.User;
import com.phcworld.phcworldboardservice.exception.model.InternalServerErrorException;
import com.phcworld.phcworldboardservice.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserConsumerImpl {
    private final UserRepository userRepository;
    private final ObjectMapper mapper;

    @KafkaListener(topics = "users")
    @Transactional
    public User getUser(String kafkaMessage){
        log.info("kafka message : -> {}", kafkaMessage);

        Map<Object, Object> map = new HashMap<>();
        try {
            JsonNode rootNode = mapper.readTree(kafkaMessage);
            JsonNode payloadNode = rootNode.get("payload");
            map = mapper.convertValue(payloadNode, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException e){
            throw new InternalServerErrorException();
        }

        String userId = (String) map.get("user_id");
        String name = (String) map.get("name");
        String profileImage = (String) map.get("profile_image");
        Optional<User> user = userRepository.findById(userId);
        User savedUser = null;
        if(user.isEmpty()){
            savedUser = userRepository.save(User.builder()
                            .userId(userId)
                            .name(name)
                            .profileImage(profileImage)
                    .build());
        } else {
            savedUser = user.get().update(name);
            userRepository.save(savedUser);
        }
        return savedUser;
    }
}
