package com.example.fdserver.service;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

    @Scheduled(fixedRateString = "${fd.timings.fixed-rate}", timeUnit = TimeUnit.SECONDS, initialDelayString = "${fd.timings.initial-delay}")
    public void calculateAverages() {
        log.info("calculate averages");
    }
}
