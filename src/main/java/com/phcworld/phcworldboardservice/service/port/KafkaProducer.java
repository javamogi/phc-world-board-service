package com.phcworld.phcworldboardservice.service.port;

import com.phcworld.phcworldboardservice.domain.FreeBoard;

public interface KafkaProducer {
    FreeBoard send(String topic, FreeBoard board, boolean isUpdate);
}
