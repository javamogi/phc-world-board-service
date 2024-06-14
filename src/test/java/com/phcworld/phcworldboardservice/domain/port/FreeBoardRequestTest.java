package com.phcworld.phcworldboardservice.domain.port;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FreeBoardRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setup(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("제목 입력값 없음")
    void emptyTitle(){
        FreeBoardRequest request = FreeBoardRequest.builder()
                .title("")
                .contents("내용")
                .build();

        Set<ConstraintViolation<FreeBoardRequest>> constraintViolations =
                validator.validate(request);

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("제목을 입력하세요.");
    }

    @Test
    @DisplayName("내용 입력값 없음")
    void emptyContents(){
        FreeBoardRequest request = FreeBoardRequest.builder()
                .title("제목")
                .contents("")
                .build();

        Set<ConstraintViolation<FreeBoardRequest>> constraintViolations =
                validator.validate(request);

        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("내용을 입력하세요.");
    }

}