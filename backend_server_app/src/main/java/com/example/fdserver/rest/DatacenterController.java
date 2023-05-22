package com.example.fdserver.rest;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.fdserver.model.Datacenter;
import com.example.fdserver.rest.model.entities.DatacenterDto;
import com.example.fdserver.service.EntitiesCrudServer;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/datacenters/")
@RequiredArgsConstructor
public class DatacenterController {

    private final EntitiesCrudServer entitiesCrudServer;

    @PostMapping("overwriteAll")
    public void overwriteAll(@RequestBody List<Datacenter> datacenters) {
        entitiesCrudServer.overwriteAll(datacenters);
    }

    @GetMapping("entities")
    public List<Datacenter> getAllEntities() {
        return entitiesCrudServer.getAllEntities();
    }

    @GetMapping
    public List<DatacenterDto> getAll() {
        return entitiesCrudServer.getAllDataCenters();
    }
}
