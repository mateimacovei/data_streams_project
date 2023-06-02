package com.example.fdserver.service;

import com.example.fdserver.repo.steams.*;
import com.example.fdserver.rest.model.streams.InputProcessorTemperatureDto;
import com.example.fdserver.rest.model.streams.InputWaterReadingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.example.fdserver.service.DtoConvertor.*;

@Component
@RequiredArgsConstructor
public class StreamService {
    private final IncidentRepo incidentRepo;

    private final ProcessorTemperatureAverageRepo processorTemperatureAverageRepo;
    private final ProcessorTemperatureRepo processorTemperatureRepo;

    private final WaterTemperatureAverageRepo waterTemperatureAverageRepo;
    private final WaterTemperatureRepo waterTemperatureRepo;

    private final WaterFlowAverageRepo waterFlowAverageRepo;
    private final WaterFlowRepo waterFlowRepo;

    public void inputProcessorTemperature(InputProcessorTemperatureDto inputProcessorTemperatureDto) {
        processorTemperatureRepo.save(fromDto(inputProcessorTemperatureDto));
    }

    public void inputWaterReading(InputWaterReadingDto inputWaterReadingDto) {
        waterTemperatureRepo.save(fromDtoToTemperature(inputWaterReadingDto));
        waterFlowRepo.save(fromDtoToFlow(inputWaterReadingDto));
    }
}
