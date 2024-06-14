package com.phcworld.phcworldboardservice.messagequeue.consumer;

public interface KafkaConsumer {
    void updateCountOfAnswer(String kafkaMessage);
}
