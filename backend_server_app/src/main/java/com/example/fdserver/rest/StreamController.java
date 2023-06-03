package com.example.fdserver.rest;

import com.example.fdserver.rest.model.report.*;
import com.example.fdserver.rest.model.streams.InputProcessorTemperatureDto;
import com.example.fdserver.rest.model.streams.InputWaterReadingDto;
import com.example.fdserver.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
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

    @PostMapping("reportProcessorTemp")
    public LineChartFullReportDto reportProcessorTemp(@RequestBody RequestReport requestReport) {
        return service.reportProcessorTemp(requestReport);
    }

    @PostMapping("reportWaterTemp")
    public LineChartFullReportDto reportWaterTemp(@RequestBody RequestReport reportWaterTemp) {
        return service.reportWaterTemp(reportWaterTemp);
    }

    @PostMapping("reportWaterFlow")
    public LineChartFullReportDto reportWaterFlow(@RequestBody RequestReport reportWaterFlow) {
        return service.reportWaterFlow(reportWaterFlow);
    }

    @PostMapping ("incidentsReport")
    public IncidentsReport incidentsReport(@RequestBody ReportIncidentsRequest reportIncidentsRequest){
        return service.reportIncidents(reportIncidentsRequest);
    }
}
