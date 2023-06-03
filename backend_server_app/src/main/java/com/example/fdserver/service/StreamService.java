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
import com.example.fdserver.service.model.LineChartReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.fdserver.service.DtoConvertor.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreamService {
    private final IncidentRepo incidentRepo;

    private final ProcessorRepo processorRepo;
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
            return lineChartReportBuilder(processorTemperatureAverageRepo.findByRack(requestCpuTemperature.getIdentifierId(), requestCpuTemperature.getFrom(), requestCpuTemperature.getTo()).stream()
                    .collect(Collectors.groupingBy(ProcessorTemperatureAverage::getProcessorId)).entrySet().stream()
                    .map(x -> LineChartReport.builder()
                            .label(processorRepo.findById(x.getKey()).map(Processor::getLabel).orElse(""))
                            .data(x.getValue().stream().sorted(Comparator.comparing(ProcessorTemperatureAverage::getInsertionDate))
                                    .map(v -> Map.entry(Timestamp.valueOf(v.getInsertionDate()).getTime() / 1000.0, v.getTemperature())).toList())
                            .build()).toList(), requestCpuTemperature);
        } else {
            return lineChartReportBuilder(processorTemperatureRepo.findByRack(requestCpuTemperature.getIdentifierId(), requestCpuTemperature.getFrom(), requestCpuTemperature.getTo()).stream()
                    .collect(Collectors.groupingBy(ProcessorTemperature::getProcessorId)).entrySet().stream()
                    .map(x -> LineChartReport.builder()
                            .label(processorRepo.findById(x.getKey()).map(Processor::getLabel).orElse(""))
                            .data(x.getValue().stream().sorted(Comparator.comparing(ProcessorTemperature::getInsertionDate))
                                    .map(v -> Map.entry(Timestamp.valueOf(v.getInsertionDate()).getTime() / 1000.0, v.getTemperature())).toList())
                            .build()).toList(), requestCpuTemperature);
        }
    }

    public LineChartFullReportDto reportWaterTemp(RequestReport reportWaterTemp) {
        if (reportWaterTemp.getValuesType() == ValuesType.AVERAGE) {
            return lineChartReportBuilder(waterTemperatureAverageRepo.findByDatacenter(reportWaterTemp.getIdentifierId(), reportWaterTemp.getFrom(), reportWaterTemp.getTo()).stream()
                    .collect(Collectors.groupingBy(WaterTemperatureAverage::getRackId)).entrySet().stream()
                    .map(x -> LineChartReport.builder()
                            .label(rackRepo.findById(x.getKey()).map(Rack::getName).orElse(""))
                            .data(x.getValue().stream().sorted(Comparator.comparing(WaterTemperatureAverage::getInsertionDate))
                                    .map(v -> Map.entry(Timestamp.valueOf(v.getInsertionDate()).getTime() / 1000.0, v.getTemperature())).toList())
                            .build()).toList(), reportWaterTemp);
        } else {
            return lineChartReportBuilder(waterTemperatureRepo.findByDatacenter(reportWaterTemp.getIdentifierId(), reportWaterTemp.getFrom(), reportWaterTemp.getTo()).stream()
                    .collect(Collectors.groupingBy(WaterTemperature::getRackId)).entrySet().stream()
                    .map(x -> LineChartReport.builder()
                            .label(rackRepo.findById(x.getKey()).map(Rack::getName).orElse(""))
                            .data(x.getValue().stream().sorted(Comparator.comparing(WaterTemperature::getInsertionDate))
                                    .map(v -> Map.entry(Timestamp.valueOf(v.getInsertionDate()).getTime() / 1000.0, v.getTemperature())).toList())
                            .build()).toList(), reportWaterTemp);
        }
    }

    public LineChartFullReportDto reportWaterFlow(RequestReport reportWaterFlow) {
        if (reportWaterFlow.getValuesType() == ValuesType.AVERAGE) {
            return lineChartReportBuilder(waterFlowAverageRepo.findByDatacenter(reportWaterFlow.getIdentifierId(), reportWaterFlow.getFrom(), reportWaterFlow.getTo()).stream()
                    .collect(Collectors.groupingBy(WaterFlowAverage::getRackId)).entrySet().stream()
                    .map(x -> LineChartReport.builder()
                            .label(rackRepo.findById(x.getKey()).map(Rack::getName).orElse(""))
                            .data(x.getValue().stream().sorted(Comparator.comparing(WaterFlowAverage::getInsertionDate))
                                    .map(v -> Map.entry(Timestamp.valueOf(v.getInsertionDate()).getTime() / 1000.0, v.getLitersPerHour())).toList())
                            .build()).toList(), reportWaterFlow);
        } else {
            return lineChartReportBuilder(waterFlowRepo.findByDatacenter(reportWaterFlow.getIdentifierId(), reportWaterFlow.getFrom(), reportWaterFlow.getTo()).stream()
                    .collect(Collectors.groupingBy(WaterFlow::getRackId)).entrySet().stream()
                    .map(x -> LineChartReport.builder()
                            .label(rackRepo.findById(x.getKey()).map(Rack::getName).orElse(""))
                            .data(x.getValue().stream().sorted(Comparator.comparing(WaterFlow::getInsertionDate))
                                    .map(v -> Map.entry(Timestamp.valueOf(v.getInsertionDate()).getTime() / 1000.0, v.getLitersPerHour())).toList())
                            .build()).toList(), reportWaterFlow);
        }
    }

    private LineChartFullReportDto lineChartReportSimpleBuilder(List<LineChartReport> datasets) {
        return LineChartFullReportDto.builder()
                .labels(IntStream.rangeClosed(1, datasets.stream().mapToInt(x -> x.getData().size()).max().orElse(1)).boxed().toList())
                .datasets(datasets.stream().map(x -> LineChartReportDto.builder()
                                .label(x.getLabel())
                                .data(x.getData().stream().map(Map.Entry::getValue).toList())
                                .build())
                        .toList())
                .build();
    }

    private LineChartFullReportDto lineChartReportBuilder(List<LineChartReport> datasets, RequestReport reportRequest) {
        if (reportRequest.getValuesType() == ValuesType.VALUE_INTERPOLATED || reportRequest.getValuesType() == ValuesType.AVERAGE_INTERPOLATED) {
            if (reportRequest.getInterpolationPoints() == null || reportRequest.getInterpolationPoints() < 3) {
                reportRequest.setInterpolationPoints(3);
            }
            try {
                // I don't have to do the optional check, sine there will always be at least a value
                var minDate = datasets.stream().flatMapToDouble(x -> x.getData().stream().mapToDouble(Map.Entry::getKey)).min().getAsDouble();
                var maxDate = datasets.stream().flatMapToDouble(x -> x.getData().stream().mapToDouble(Map.Entry::getKey)).max().getAsDouble();

                var evaluationPoints = generateEqualDistancePoints(minDate, maxDate, reportRequest.getInterpolationPoints());


                var datasetsDtos = datasets.stream().map(d -> {
                    Map.Entry<Double, Integer> localMin = getMin(d.getData());
                    Map.Entry<Double, Integer> localMax = geMax(d.getData());
                    PolynomialSplineFunction interpolatorFunction = new SplineInterpolator()
                            .interpolate(d.getData().stream().mapToDouble(Map.Entry::getKey).toArray(),
                                    d.getData().stream().mapToDouble(x -> x.getValue().doubleValue()).toArray());

                    return LineChartReportDto.builder()
                            .label(d.getLabel())
                            .data(evaluationPoints.stream().map(x -> getInterpolationResult(localMin, localMax, interpolatorFunction, x)).toList())
                            .build();
                }).toList();
                return LineChartFullReportDto.builder()
                        .labels(IntStream.rangeClosed(1, datasetsDtos.stream().mapToInt(x -> x.getData().size()).max().orElse(1)).boxed().toList())
                        .datasets(datasetsDtos)
                        .build();

            } catch (Exception ex) {
                log.error("Interpolation failed", ex);
            }
        }
        return lineChartReportSimpleBuilder(datasets);
    }

    private Map.Entry<Double, Integer> geMax(List<Map.Entry<Double, Integer>> data) {
        var min = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            if (min.getKey().compareTo(data.get(i).getKey()) < 0) {
                min = data.get(i);
            }
        }
        return min;
    }

    private Map.Entry<Double, Integer> getMin(List<Map.Entry<Double, Integer>> data) {
        var max = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            if (max.getKey().compareTo(data.get(i).getKey()) > 0) {
                max = data.get(i);
            }
        }
        return max;
    }

    /**
     * use the nearest neighbour for keys outside the interval and the polynomialSplineFunction for keys in the interval
     */
    private static Integer getInterpolationResult(Map.Entry<Double, Integer> min, Map.Entry<Double, Integer> max, PolynomialSplineFunction interpolatorFunction, Double evaluationPoint) {
        if (evaluationPoint.compareTo(min.getKey()) <= 0) {
            return min.getValue();
        }
        if (evaluationPoint.compareTo(max.getKey()) >= 0) {
            return max.getValue();
        }

        return (int) interpolatorFunction.value(evaluationPoint);
    }

    public static List<Double> generateEqualDistancePoints(double a, double b, int numPoints) {
        double intervalLength = b - a;
        double pointDistance = intervalLength / (numPoints - 1);

        double[] points = new double[numPoints];
        points[0] = a;

        for (int i = 1; i < numPoints; i++) {
            points[i] = points[i - 1] + pointDistance;
        }

        return Arrays.stream(points).boxed().toList();
    }
}
