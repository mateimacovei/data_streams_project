package com.example.fdserver.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.example.fdserver.model.events.AverageCpuTempInserted;
import com.example.fdserver.model.events.ProcessorTemperatureInserted;
import com.example.fdserver.model.events.WaterFlowInserted;
import com.example.fdserver.model.streams.*;
import com.example.fdserver.repo.ProcessorRepo;
import com.example.fdserver.repo.RackRepo;
import com.example.fdserver.repo.steams.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {
    private final ProcessorRepo processorRepo;
    private final RackRepo rackRepo;

    private final IncidentRepo incidentRepo;

    private final ProcessorTemperatureAverageRepo processorTemperatureAverageRepo;
    private final ProcessorTemperatureRepo processorTemperatureRepo;

    private final WaterTemperatureAverageRepo waterTemperatureAverageRepo;
    private final WaterTemperatureRepo waterTemperatureRepo;

    private final WaterFlowAverageRepo waterFlowAverageRepo;
    private final WaterFlowRepo waterFlowRepo;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Scheduled(fixedRateString = "${fd.timings.fixed-rate}", timeUnit = TimeUnit.SECONDS, initialDelayString = "${fd.timings.initial-delay}")
    public void calculateAverages() {
        log.info("calculate averages started");
        var startOfInterval = LocalDateTime.now().minus(30, ChronoUnit.MINUTES);
        var processorTempAverages = processorTemperatureRepo.findAllByInsertionDateAfter(startOfInterval).stream()
                .collect(Collectors.groupingBy(ProcessorTemperature::getProcessorId)).entrySet()
                .stream().filter(x -> !x.getValue().isEmpty())
                .map(x -> ProcessorTemperatureAverage.builder().processorId(x.getKey())
                        .temperature(x.getValue().stream().mapToInt(ProcessorTemperature::getTemperature).sum() / x.getValue().size())
                        .build()).toList();
        processorTemperatureAverageRepo.saveAll(processorTempAverages);

        var waterTemperatureAverages = waterTemperatureRepo.findAllByInsertionDateAfter(startOfInterval).stream()
                .collect(Collectors.groupingBy(WaterTemperature::getRackId)).entrySet()
                .stream().filter(x -> !x.getValue().isEmpty())
                .map(x -> WaterTemperatureAverage.builder().rackId(x.getKey())
                        .temperature(x.getValue().stream().mapToInt(WaterTemperature::getTemperature).sum() / x.getValue().size())
                        .build()).toList();
        waterTemperatureAverageRepo.saveAll(waterTemperatureAverages);

        var waterFlowAverages = waterFlowRepo.findAllByInsertionDateAfter(startOfInterval).stream()
                .collect(Collectors.groupingBy(WaterFlow::getRackId)).entrySet()
                .stream().filter(x -> !x.getValue().isEmpty())
                .map(x -> WaterFlowAverage.builder().rackId(x.getKey())
                        .litersPerHour(x.getValue().stream().mapToInt(WaterFlow::getLitersPerHour).sum() / x.getValue().size())
                        .build()).toList();
        waterFlowAverageRepo.saveAll(waterFlowAverages);

        processorTempAverages.forEach(x->applicationEventPublisher.publishEvent(new AverageCpuTempInserted(x.getProcessorId())));

        log.info("calculate averages ended");
    }

    @Async
    @EventListener
    public void processorTemperatureInserted(ProcessorTemperatureInserted processorTemperatureInserted) {
        log.debug("processorTemperatureInserted");
        var startOfInterval = LocalDateTime.now().minus(5, ChronoUnit.MINUTES);
        var readings = processorTemperatureRepo.findAllByProcessorIdAndInsertionDateAfter(processorTemperatureInserted.getProcessorId(), startOfInterval);
        if (!readings.isEmpty()) {
            Integer avg = readings.stream().mapToInt(ProcessorTemperature::getTemperature).sum() / readings.size();
            if (avg.compareTo(100) > 0 &&
                    !incidentRepo.existsIncidentsByProcessorIdAndIncidentTypeAndInsertionDateAfter(processorTemperatureInserted.getProcessorId(), IncidentType.MAX_TEMP, LocalDateTime.now().minus(10, ChronoUnit.MINUTES))) {
                log.info("MAX_TEMP incident");
                processorRepo.findById(processorTemperatureInserted.getProcessorId())
                        .ifPresent(p -> incidentRepo.save(Incident.builder()
                                .processorId(p.getId())
                                .rackId(p.getRack().getId())
                                .datacenterId(p.getRack().getDatacenter().getId())
                                .incidentType(IncidentType.MAX_TEMP)
                                .incidentValue(avg.toString())
                                .build()));
            }
        }
    }

    @Async
    @EventListener
    public void waterFlowInserted(WaterFlowInserted waterFlowInserted) {
        log.debug("waterFlowInserted");
        var startOfInterval = LocalDateTime.now().minus(5, ChronoUnit.MINUTES);
        var readings = waterFlowRepo.findAllByRackIdAndInsertionDateAfter(waterFlowInserted.getRackId(), startOfInterval);
        if (!readings.isEmpty()) {
            Integer avg = readings.stream().mapToInt(WaterFlow::getLitersPerHour).sum() / readings.size();
            if (avg.compareTo(5) < 0 &&
                    !incidentRepo.existsIncidentsByRackIdAndIncidentTypeAndInsertionDateAfter(waterFlowInserted.getRackId(), IncidentType.NO_FLOW, LocalDateTime.now().minus(10, ChronoUnit.MINUTES))) {
                log.info("NO_FLOW incident");
                rackRepo.findById(waterFlowInserted.getRackId())
                        .ifPresent(r -> incidentRepo.save(Incident.builder()
                                .rackId(r.getId())
                                .datacenterId(r.getDatacenter().getId())
                                .incidentType(IncidentType.NO_FLOW)
                                .incidentValue(avg.toString())
                                .build()));
            }
        }
    }

    @Async
    @EventListener
    public void averageCpuTempInserted(AverageCpuTempInserted averageCpuTempInserted) {
        log.debug("averageCpuTempInserted");
        processorRepo.findById(averageCpuTempInserted.getProcessorId()).ifPresent(p -> {
            processorTemperatureAverageRepo.findTopByProcessorIdOrderByInsertionDateDesc(p.getId())
                    .ifPresent(avgCpuTemp -> waterTemperatureAverageRepo.findTopByRackIdOrderByInsertionDateDesc(p.getRack().getId())
                            .ifPresent(avgWaterTemp -> {
                                Integer diff = avgCpuTemp.getTemperature() - avgWaterTemp.getTemperature();
                                if (diff.compareTo(20) > 0 &&
                                        !incidentRepo.existsIncidentsByProcessorIdAndIncidentTypeAndInsertionDateAfter(p.getId(), IncidentType.BAD_CONTACT, LocalDateTime.now().minus(10, ChronoUnit.MINUTES))) {
                                    log.info("BAD_CONTACT incident");
                                    incidentRepo.save(Incident.builder()
                                            .processorId(p.getId())
                                            .rackId(p.getRack().getId())
                                            .datacenterId(p.getRack().getDatacenter().getId())
                                            .incidentType(IncidentType.BAD_CONTACT)
                                            .incidentValue(diff.toString())
                                            .build());
                                }
                            }));
        });
    }
}
