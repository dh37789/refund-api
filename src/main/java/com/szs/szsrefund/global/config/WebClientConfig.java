package com.szs.szsrefund.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Value("${3o3.base-url}")
    private String baseUrl;

    @Bean
    public WebClient webClient() {
        /* 20초 timeout 설정 */
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(20));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
