package com.example.fdserver.service;

import com.example.fdserver.model.Datacenter;
import com.example.fdserver.model.Rack;
import com.example.fdserver.rest.model.entities.DatacenterDto;
import com.example.fdserver.rest.model.entities.RackDto;

public class DtoConvertor {

    private DtoConvertor() {
    }

    static DatacenterDto fromEntity(Datacenter entity) {
        return DatacenterDto.builder().id(entity.getId()).name(entity.getName()).build();
    }

    static RackDto fromEntity(Rack entity) {
        return RackDto.builder().id(entity.getId()).name(entity.getName()).build();
    }
}
