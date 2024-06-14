package com.phcworld.phcworldboardservice.mock;

import com.phcworld.phcworldboardservice.controller.FreeBoardApiController;
import com.phcworld.phcworldboardservice.controller.port.FreeBoardService;
import com.phcworld.phcworldboardservice.controller.port.WebclientService;
import com.phcworld.phcworldboardservice.service.FreeBoardServiceImpl;
import com.phcworld.phcworldboardservice.service.port.FreeBoardRepository;
import com.phcworld.phcworldboardservice.service.port.LocalDateTimeHolder;
import lombok.Builder;

public class TestContainer {

    public final FreeBoardRepository freeBoardRepository;

    public final FreeBoardService freeBoardService;

    public final FreeBoardApiController freeBoardApiController;

    public final WebclientService webclientService;

    @Builder
    public TestContainer(LocalDateTimeHolder localDateTimeHolder){
        this.freeBoardRepository = new FakeFreeBoardRepository();
        this.webclientService = new FakeWebClientService();
        this.freeBoardService = FreeBoardServiceImpl.builder()
                .freeBoardRepository(freeBoardRepository)
                .localDateTimeHolder(localDateTimeHolder)
                .boardProducer(new FakeKafkaProducer())
                .build();
        this.freeBoardApiController = FreeBoardApiController.builder()
                .webclientService(webclientService)
                .freeBoardService(freeBoardService)
                .build();
    }

}
