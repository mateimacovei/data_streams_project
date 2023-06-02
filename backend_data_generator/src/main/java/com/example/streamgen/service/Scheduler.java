package com.example.streamgen.service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.streamgen.model.Datacenter;
import com.example.streamgen.model.streams.ProcessorTemperatureDto;
import com.example.streamgen.model.streams.WaterReadingDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient processorReadingWebClient;
    private final WebClient waterBlockReadingWebClient;
    @Value("${fd.processor-temp.default-min}")
    private Integer processorTempMinDefault;
    @Value("${fd.processor-temp.default-max}")
    private Integer processorTempMaxDefault;
    @Value("${fd.water-temp.default-min}")
    private Integer waterTempMinDefault;
    @Value("${fd.water-temp.default-max}")
    private Integer waterTempMaxDefault;
    @Value("${fd.water-flow.default-min}")
    private Integer waterFlowMinDefault;
    @Value("${fd.water-flow.default-max}")
    private Integer waterFlowMaxDefault;

    @Scheduled(fixedRateString = "${fd.timings.fixed-rate}", timeUnit = TimeUnit.SECONDS, initialDelayString = "${fd.timings.initial-delay}")
    public void generateData() {
        log.info("Starting cycle at {}", LocalDateTime.now());
        try (InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("static/entities.json")) {
            var datacenters = Arrays.stream(objectMapper.readValue(in, Datacenter[].class)).toList();
            datacenters.forEach(d -> d.getRacks().forEach(rack -> {
                var waterBlockReading = WaterReadingDto.builder()
                        .rackId(rack.getId())
                        .temperature(getWaterTemperature(rack.getWaterTemperatureMin(), rack.getWaterTemperatureMax()))
                        .litersPerHour(getWaterFlow(rack.getWaterFlowMin(), rack.getWaterFlowMax()))
                        .build();
                // send waterBlock reading
                waterBlockReadingWebClient.post()
                        .body(BodyInserters.fromValue(waterBlockReading))
                        .retrieve()
                        .bodyToMono(Void.class)
                        .subscribe();
                rack.getProcessors().forEach(p -> {
                    var processorReading = ProcessorTemperatureDto.builder()
                            .processorId(p.getId())
                            .temperature(getProcessorTemperature(p.getTemperatureMin(), p.getTemperatureMax()))
                            .build();
                    // send processor reading
                    processorReadingWebClient.post()
                            .body(BodyInserters.fromValue(processorReading))
                            .retrieve()
                            .bodyToMono(Void.class)
                            .subscribe();
                });
            }));
        } catch (Exception e) {
            log.error("data generation failed", e);
        }

        log.info("Ending cycle at {}", LocalDateTime.now());
    }

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private Integer getProcessorTemperature(Integer temperatureMin, Integer temperatureMax) {
        return getRandomNumber(temperatureMin != null ? temperatureMin : processorTempMinDefault,
                temperatureMax != null ? temperatureMax : processorTempMaxDefault);
    }

    private Integer getWaterFlow(Integer waterFlowMin, Integer waterFlowMax) {
        return getRandomNumber(waterFlowMin != null ? waterFlowMin : waterFlowMinDefault,
                waterFlowMax != null ? waterFlowMax : waterFlowMaxDefault);
    }

    private Integer getWaterTemperature(Integer waterTemperatureMin, Integer waterTemperatureMax) {
        return getRandomNumber(waterTemperatureMin != null ? waterTemperatureMin : waterTempMinDefault,
                waterTemperatureMax != null ? waterTemperatureMax : waterTempMaxDefault);
    }
}
