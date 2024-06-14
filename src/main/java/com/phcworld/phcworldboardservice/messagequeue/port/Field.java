package com.phcworld.phcworldboardservice.messagequeue.port;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Field {
    private String type;
    private boolean optional;
    private String field;

}
