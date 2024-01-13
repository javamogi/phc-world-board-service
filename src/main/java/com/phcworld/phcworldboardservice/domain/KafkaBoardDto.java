package com.phcworld.phcworldboardservice.domain;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class KafkaBoardDto implements Serializable {
    private Schema schema;
    private Payload payload;
}
