package com.example.fdserver.rest;

import com.example.fdserver.rest.model.streams.InputProcessorTemperatureDto;
import com.example.fdserver.rest.model.streams.InputWaterReadingDto;
import com.example.fdserver.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/stream/")
@RequiredArgsConstructor
public class StreamController {
    private final StreamService service;

    @PostMapping("processorTemperature")
    public void inputProcessorTemperature(@RequestBody InputProcessorTemperatureDto inputProcessorTemperatureDto) {
        service.inputProcessorTemperature(inputProcessorTemperatureDto);
    }

    @PostMapping("waterReading")
    public void inputWaterReading(@RequestBody InputWaterReadingDto inputWaterReadingDto) {
        service.inputWaterReading(inputWaterReadingDto);
    }
}
