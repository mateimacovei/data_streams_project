package com.example.fdserver.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fdserver.rest.model.entities.RackDto;
import com.example.fdserver.service.EntitiesCrudServer;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/racks/")
@RequiredArgsConstructor
public class RackController {

    private final EntitiesCrudServer entitiesCrudServer;

    @GetMapping
    public List<RackDto> getAll(@RequestParam Long datacenterId) {
        return entitiesCrudServer.getAllRacks(datacenterId);
    }
}
