package com.example.streamgen.service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.streamgen.model.Datacenter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${fd.processor-temp.default-min}")
    private Long processorTempMin;
    @Value("${fd.processor-temp.default-max}")
    private Long processorTempMax;

    @Value("${fd.water-temp.default-min}")
    private Long waterTempMin;
    @Value("${fd.water-temp.default-max}")
    private Long waterTempMax;

    @Value("${fd.water-flow.default-min}")
    private Long waterFlowMin;
    @Value("${fd.water-flow.default-max}")
    private Long waterFlowMax;

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    @Scheduled(fixedRateString = "${fd.timings.fixed-rate}", timeUnit = TimeUnit.SECONDS, initialDelayString = "${fd.timings.initial-delay}")
    public void calculateAverages() {
        log.info("Starting cycle at {}", LocalDateTime.now());
        try (InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("static/entities.json")) {
            var datacenters = objectMapper.readValue(in, Datacenter[].class);
            System.out.println(datacenters[0].getName());
        } catch (Exception e) {
            log.error("data generation failed", e);
        }

        log.info("Ending cycle at {}", LocalDateTime.now());
    }
}
