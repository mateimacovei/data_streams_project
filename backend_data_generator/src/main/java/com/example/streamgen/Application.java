package com.example.streamgen;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;

@EnableScheduling
@SpringBootApplication
public class Application {

    @Value("${fd.server-url}")
    private String serverUrl;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public WebClient processorReadingWebClient() {
        return WebClient.builder().baseUrl(serverUrl + "/api/stream/processorTemperature").build();
    }

    @Bean
    public WebClient waterBlockReadingWebClient() {
        return WebClient.builder().baseUrl(serverUrl + "/api/stream/waterReading").build();
    }

}
