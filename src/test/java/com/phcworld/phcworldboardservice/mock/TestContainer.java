package com.phcworld.phcworldboardservice.mock;

import com.phcworld.phcworldboardservice.controller.FreeBoardCommandApiController;
import com.phcworld.phcworldboardservice.controller.FreeBoardQueryApiController;
import com.phcworld.phcworldboardservice.controller.port.FreeBoardService;
import com.phcworld.phcworldboardservice.controller.port.WebclientService;
import com.phcworld.phcworldboardservice.service.FreeBoardServiceImpl;
import com.phcworld.phcworldboardservice.service.port.FreeBoardRepository;
import com.phcworld.phcworldboardservice.service.port.LocalDateTimeHolder;
import com.phcworld.phcworldboardservice.service.port.UuidHolder;
import lombok.Builder;

public class TestContainer {

    public final FreeBoardRepository freeBoardRepository;

    public final FreeBoardService freeBoardService;

    public final FreeBoardCommandApiController freeBoardCommandApiController;

    public final WebclientService webclientService;

    public final FreeBoardQueryApiController freeBoardQueryApiController;

    @Builder
    public TestContainer(LocalDateTimeHolder localDateTimeHolder, UuidHolder uuidHolder){
        this.freeBoardRepository = new FakeFreeBoardRepository();
        this.webclientService = new FakeWebClientService();
        this.freeBoardService = FreeBoardServiceImpl.builder()
                .uuidHolder(uuidHolder)
                .freeBoardRepository(freeBoardRepository)
                .localDateTimeHolder(localDateTimeHolder)
                .boardProducer(new FakeKafkaProducer())
                .build();
        this.freeBoardQueryApiController = FreeBoardQueryApiController.builder()
                .webclientService(webclientService)
                .freeBoardService(freeBoardService)
                .build();
        this.freeBoardCommandApiController = FreeBoardCommandApiController.builder()
                .webclientService(webclientService)
                .freeBoardService(freeBoardService)
                .build();
    }

}
