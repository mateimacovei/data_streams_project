package com.example.fdserver.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.fdserver.model.Datacenter;
import com.example.fdserver.repo.DatacenterRepo;
import com.example.fdserver.repo.RackRepo;
import com.example.fdserver.repo.steams.*;
import com.example.fdserver.rest.model.entities.DatacenterDto;
import com.example.fdserver.rest.model.entities.RackDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EntitiesCrudServer {

    private final DatacenterRepo datacenterRepo;
    private final RackRepo rackRepo;
    private final IncidentRepo incidentRepo;
    private final WaterFlowRepo waterFlowRepo;
    private final WaterFlowAverageRepo waterFlowAverageRepo;
    private final WaterTemperatureRepo waterTemperatureRepo;
    private final WaterTemperatureAverageRepo waterTemperatureAverageRepo;
    private final ProcessorTemperatureRepo processorTemperatureRepo;
    private final ProcessorTemperatureAverageRepo processorTemperatureAverageRepo;

    public void overwriteAll(List<Datacenter> datacenters) {
        datacenterRepo.deleteAll();

        incidentRepo.deleteAll();
        waterFlowRepo.deleteAll();
        waterFlowAverageRepo.deleteAll();
        waterTemperatureRepo.deleteAll();
        waterTemperatureAverageRepo.deleteAll();
        processorTemperatureRepo.deleteAll();
        processorTemperatureAverageRepo.deleteAll();

        datacenters.forEach(d -> d.getRacks().forEach(r -> {
            r.setDatacenter(d);
            r.getProcessors().forEach(p -> p.setRack(r));
        }));
        datacenterRepo.saveAll(datacenters);
    }

    public List<DatacenterDto> getAllDataCenters() {
        return datacenterRepo.findAll().stream().map(DtoConvertor::fromEntity).collect(Collectors.toList());
    }

    public List<RackDto> getAllRacks(Long datacenterId) {
        return rackRepo.findAllByDatacenter_Id(datacenterId)
                .stream()
                .map(DtoConvertor::fromEntity)
                .collect(Collectors.toList());
    }
}
