package com.phcworld.phcworldboardservice.messagequeue.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class KafkaBoardDto implements Serializable {
    private Schema schema;
    private Payload payload;
}
