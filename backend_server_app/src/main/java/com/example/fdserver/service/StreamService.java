package com.example.fdserver.service;

import com.example.fdserver.model.Processor;
import com.example.fdserver.model.Rack;
import com.example.fdserver.model.events.ProcessorTemperatureInserted;
import com.example.fdserver.model.events.WaterFlowInserted;
import com.example.fdserver.model.streams.*;
import com.example.fdserver.repo.DatacenterRepo;
import com.example.fdserver.repo.ProcessorRepo;
import com.example.fdserver.repo.RackRepo;
import com.example.fdserver.repo.steams.*;
import com.example.fdserver.rest.model.report.*;
import com.example.fdserver.rest.model.streams.IncidentDto;
import com.example.fdserver.rest.model.streams.InputProcessorTemperatureDto;
import com.example.fdserver.rest.model.streams.InputWaterReadingDto;
import com.example.fdserver.service.model.IncidentWithNames;
import com.example.fdserver.service.model.LineChartReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.fdserver.service.DtoConvertor.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreamService {
    private final IncidentRepo incidentRepo;
    private final DatacenterRepo datacenterRepo;

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
//                    Map.Entry<Double, Integer> localMin = getMin(d.getData());
//                    Map.Entry<Double, Integer> localMax = geMax(d.getData());
//                    UnivariateFunction interpolatorFunction = new DividedDifferenceInterpolator()
//                            .interpolate(d.getData().stream().mapToDouble(Map.Entry::getKey).toArray(),
//                                    d.getData().stream().mapToDouble(x -> x.getValue().doubleValue()).toArray());

                    return LineChartReportDto.builder()
                            .label(d.getLabel())
                            .data(evaluationPoints.stream().map(x -> getInterpolationResult(d.getData().stream().map(Map.Entry::getKey).toList(), d.getData().stream().map(Map.Entry::getValue).toList(), x)).toList())
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

    /**
     * use the nearest neighbour
     */
    private static Integer getInterpolationResult(List<Double> keys, List<Integer> values, Double evaluationPoint) {
        var nearestNeighbourIndex = 0;
        var absMinDiff = Double.MAX_VALUE;
        for (int i = 1; i < keys.size(); i++) {
            var diff = Math.abs(evaluationPoint - keys.get(i));
            if (diff < absMinDiff) {
                absMinDiff = diff;
                nearestNeighbourIndex = i;
            }
        }
        return values.get(nearestNeighbourIndex);
    }

//    /**
//     * use the nearest neighbour for keys outside the interval and the polynomialSplineFunction for keys in the interval
//     */
//    private static Integer getInterpolationResult(Map.Entry<Double, Integer> min, Map.Entry<Double, Integer> max, UnivariateFunction interpolatorFunction, Double evaluationPoint) {
//        if (evaluationPoint.compareTo(min.getKey()) <= 0) {
//            return min.getValue();
//        }
//        if (evaluationPoint.compareTo(max.getKey()) >= 0) {
//            return max.getValue();
//        }
//
//        return (int) interpolatorFunction.value(evaluationPoint);
//    }

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

    public IncidentsReport reportIncidents(ReportIncidentsRequest reportIncidentsRequest) {
        var incidents = incidentRepo.findAllByInsertionDate(reportIncidentsRequest.getFrom(), reportIncidentsRequest.getTo());
        var report = new PieChartReportDto(new ArrayList<>(), new ArrayList<>());

        var groupsByDatacenter = incidents.stream().collect(Collectors.groupingBy(IncidentWithNames::getDatacenterId)).entrySet().stream().toList();
        report.getLabels().addAll(groupsByDatacenter.stream().map(x->x.getValue().get(0).getDatacenter()).toList());
        report.getValues().add(Map.entry("data",groupsByDatacenter.stream().map(x->x.getValue().size()).toList()));

        var groupsByType = incidents.stream().collect(Collectors.groupingBy(IncidentWithNames::getIncidentType)).entrySet().stream().toList();
        report.getLabels().addAll(groupsByType.stream().map(x->switch (x.getKey()){
            case MAX_TEMP -> "Maximum cpu temperature";
            case NO_FLOW -> "Bad water flow";
            case BAD_CONTACT -> "Bad cpu contact";
        }).toList());
        report.getValues().add(Map.entry("data",groupsByType.stream().map(x->x.getValue().size()).toList()));

        return IncidentsReport.builder().incidents(incidents.stream().map(this::fromIncidentToString).toList()).pieChartReport(report).build();
    }

    private IncidentDto fromIncidentToString(IncidentWithNames incident) {
        return IncidentDto
                .builder()
                .description(switch (incident.getIncidentType()) {
                    case MAX_TEMP ->
                            "In " + incident.getDatacenterId() + " on rack " + incident.getRack() + " at processor " + incident.getProcessor() + " is at the temperature of " + incident.getIncidentValue();
                    case NO_FLOW ->
                            "Low water flow detected in " + incident.getDatacenterId() + " on rack " + incident.getRack();
                    case BAD_CONTACT ->
                            "Possible bad cpu contact detected in " + incident.getDatacenterId() + " on rack " + incident.getRack() + " at processor " + incident.getProcessor();
                })
                .date(incident.getInsertionDate().toString().substring(0,19).replace("T", " "))
                .build();
    }
}
