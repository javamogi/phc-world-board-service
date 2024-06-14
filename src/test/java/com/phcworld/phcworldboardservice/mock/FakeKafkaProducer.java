package com.phcworld.phcworldboardservice.mock;

import com.phcworld.phcworldboardservice.domain.FreeBoard;
import com.phcworld.phcworldboardservice.service.port.KafkaProducer;

public class FakeKafkaProducer implements KafkaProducer {
    @Override
    public FreeBoard send(String topic, FreeBoard board, boolean isUpdate) {
        return board;
    }
}
