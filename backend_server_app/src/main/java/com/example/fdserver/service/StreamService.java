package com.example.fdserver.service;

import com.example.fdserver.model.Processor;
import com.example.fdserver.model.Rack;
import com.example.fdserver.model.events.ProcessorTemperatureInserted;
import com.example.fdserver.model.events.WaterFlowInserted;
import com.example.fdserver.model.streams.*;
import com.example.fdserver.repo.ProcessorRepo;
import com.example.fdserver.repo.RackRepo;
import com.example.fdserver.repo.steams.*;
import com.example.fdserver.rest.model.report.*;
import com.example.fdserver.rest.model.streams.InputProcessorTemperatureDto;
import com.example.fdserver.rest.model.streams.InputWaterReadingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.stream.Collectors;

import static com.example.fdserver.service.DtoConvertor.*;

@Component
@RequiredArgsConstructor
public class StreamService {
    private final IncidentRepo incidentRepo;

    private final ProcessorRepo  processorRepo;
    private final ProcessorTemperatureAverageRepo processorTemperatureAverageRepo;
    private final ProcessorTemperatureRepo processorTemperatureRepo;

    private final RackRepo rackRepo;
    private final WaterTemperatureAverageRepo waterTemperatureAverageRepo;
    private final WaterTemperatureRepo waterTemperatureRepo;

    private final WaterFlowAverageRepo waterFlowAverageRepo;
    private final WaterFlowRepo waterFlowRepo;

    private final ApplicationEventPublisher applicationEventPublisher;

    public void inputProcessorTemperature(InputProcessorTemperatureDto inputProcessorTemperatureDto) {
        processorTemperatureRepo.save(fromDto(inputProcessorTemperatureDto));
        applicationEventPublisher.publishEvent(new ProcessorTemperatureInserted(inputProcessorTemperatureDto.getProcessorId()));
    }

    public void inputWaterReading(InputWaterReadingDto inputWaterReadingDto) {
        waterTemperatureRepo.save(fromDtoToTemperature(inputWaterReadingDto));
        waterFlowRepo.save(fromDtoToFlow(inputWaterReadingDto));
        applicationEventPublisher.publishEvent(new WaterFlowInserted(inputWaterReadingDto.getRackId()));
    }

    public LineChartFullReportDto reportProcessorTemp(RequestReport requestCpuTemperature) {
        if (requestCpuTemperature.getValuesType() == ValuesType.AVERAGE) {
            return new LineChartFullReportDto(processorTemperatureAverageRepo.findByRack(requestCpuTemperature.getIdentifierId(), requestCpuTemperature.getFrom(), requestCpuTemperature.getTo()).stream()
                    .collect(Collectors.groupingBy(ProcessorTemperatureAverage::getProcessorId)).entrySet().stream()
                    .map(x->LineChartReportDto.builder()
                            .label(processorRepo.findById(x.getKey()).map(Processor::getLabel).orElse(""))
                            .data(x.getValue().stream().sorted(Comparator.comparing(ProcessorTemperatureAverage::getInsertionDate))
                                    .map(ProcessorTemperatureAverage::getTemperature).toList())
                            .build()).toList());
        } else {
            return new LineChartFullReportDto(processorTemperatureRepo.findByRack(requestCpuTemperature.getIdentifierId(), requestCpuTemperature.getFrom(), requestCpuTemperature.getTo()).stream()
                    .collect(Collectors.groupingBy(ProcessorTemperature::getProcessorId)).entrySet().stream()
                    .map(x->LineChartReportDto.builder()
                            .label(processorRepo.findById(x.getKey()).map(Processor::getLabel).orElse(""))
                            .data(x.getValue().stream().sorted(Comparator.comparing(ProcessorTemperature::getInsertionDate))
                                    .map(ProcessorTemperature::getTemperature).toList())
                            .build()).toList());
        }
    }

    public LineChartFullReportDto reportWaterTemp(RequestReport reportWaterTemp) {
        if (reportWaterTemp.getValuesType() == ValuesType.AVERAGE) {
            return new LineChartFullReportDto(waterTemperatureAverageRepo.findByDatacenter(reportWaterTemp.getIdentifierId(), reportWaterTemp.getFrom(), reportWaterTemp.getTo()).stream()
                    .collect(Collectors.groupingBy(WaterTemperatureAverage::getRackId)).entrySet().stream()
                    .map(x->LineChartReportDto.builder()
                            .label(rackRepo.findById(x.getKey()).map(Rack::getName).orElse(""))
                            .data(x.getValue().stream().sorted(Comparator.comparing(WaterTemperatureAverage::getInsertionDate))
                                    .map(WaterTemperatureAverage::getTemperature).toList())
                            .build()).toList());
        } else {
            return new LineChartFullReportDto(waterTemperatureRepo.findByDatacenter(reportWaterTemp.getIdentifierId(), reportWaterTemp.getFrom(), reportWaterTemp.getTo()).stream()
                    .collect(Collectors.groupingBy(WaterTemperature::getRackId)).entrySet().stream()
                    .map(x->LineChartReportDto.builder()
                            .label(rackRepo.findById(x.getKey()).map(Rack::getName).orElse(""))
                            .data(x.getValue().stream().sorted(Comparator.comparing(WaterTemperature::getInsertionDate))
                                    .map(WaterTemperature::getTemperature).toList())
                            .build()).toList());
        }
    }

    public LineChartFullReportDto reportWaterFlow(RequestReport reportWaterFlow) {
        if (reportWaterFlow.getValuesType() == ValuesType.AVERAGE) {
            return new LineChartFullReportDto(waterFlowAverageRepo.findByDatacenter(reportWaterFlow.getIdentifierId(), reportWaterFlow.getFrom(), reportWaterFlow.getTo()).stream()
                    .collect(Collectors.groupingBy(WaterFlowAverage::getRackId)).entrySet().stream()
                    .map(x->LineChartReportDto.builder()
                            .label(rackRepo.findById(x.getKey()).map(Rack::getName).orElse(""))
                            .data(x.getValue().stream().sorted(Comparator.comparing(WaterFlowAverage::getInsertionDate))
                                    .map(WaterFlowAverage::getLitersPerHour).toList())
                            .build()).toList());
        } else {
            return new LineChartFullReportDto(waterFlowRepo.findByDatacenter(reportWaterFlow.getIdentifierId(), reportWaterFlow.getFrom(), reportWaterFlow.getTo()).stream()
                    .collect(Collectors.groupingBy(WaterFlow::getRackId)).entrySet().stream()
                    .map(x->LineChartReportDto.builder()
                            .label(rackRepo.findById(x.getKey()).map(Rack::getName).orElse(""))
                            .data(x.getValue().stream().sorted(Comparator.comparing(WaterFlow::getInsertionDate))
                                    .map(WaterFlow::getLitersPerHour).toList())
                            .build()).toList());
        }
    }
}
