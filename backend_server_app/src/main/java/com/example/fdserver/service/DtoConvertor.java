package com.example.fdserver.service;

import com.example.fdserver.model.Datacenter;
import com.example.fdserver.model.Rack;
import com.example.fdserver.model.streams.ProcessorTemperature;
import com.example.fdserver.model.streams.WaterFlow;
import com.example.fdserver.model.streams.WaterTemperature;
import com.example.fdserver.rest.model.entities.DatacenterDto;
import com.example.fdserver.rest.model.entities.RackDto;
import com.example.fdserver.rest.model.streams.InputProcessorTemperatureDto;
import com.example.fdserver.rest.model.streams.InputWaterReadingDto;

public class DtoConvertor {

    private DtoConvertor() {
    }

    static DatacenterDto fromEntity(Datacenter entity) {
        return DatacenterDto.builder().id(entity.getId()).name(entity.getName()).build();
    }

    static RackDto fromEntity(Rack entity) {
        return RackDto.builder().id(entity.getId()).name(entity.getName()).build();
    }

    static ProcessorTemperature fromDto(InputProcessorTemperatureDto dto) {
        return ProcessorTemperature.builder().processorId(dto.getProcessorId()).temperature(dto.getTemperature()).build();
    }

    static WaterTemperature fromDtoToTemperature(InputWaterReadingDto dto) {
        return WaterTemperature.builder().rackId(dto.getRackId()).temperature(dto.getTemperature()).build();
    }

    static WaterFlow fromDtoToFlow(InputWaterReadingDto dto) {
        return WaterFlow.builder().rackId(dto.getRackId()).litersPerHour(dto.getLitersPerHour()).build();
    }
}
