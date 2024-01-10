package com.phcworld.phcworldboardservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class SpringConfig {

    private final Environment env;

    @Bean
    @LoadBalanced
    RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

//    @Bean
//    @LoadBalanced
//    WebClient getWebClient(){
//        return WebClient
//                .builder()
////                .baseUrl("http://localhost:12779/users")
////                .baseUrl(env.getProperty("user-api.url"))
//                .build();
//    }

    @Bean
    @LoadBalanced
    WebClient.Builder getWebClient(){
        return WebClient
                .builder();
    }
}
