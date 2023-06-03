package com.example.fdserver.service.model;

import com.example.fdserver.model.streams.IncidentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
public class IncidentWithNames {
    private Long datacenterId;
    private String datacenter;
    private String rack;
    private String processor;

    private IncidentType incidentType;
    private String incidentValue;

    private LocalDateTime insertionDate;

    public IncidentWithNames(Long datacenterId, String datacenter, String rack, String processor, IncidentType incidentType, String incidentValue, LocalDateTime insertionDate) {
        this.datacenterId = datacenterId;
        this.datacenter = datacenter;
        this.rack = rack;
        this.processor = processor;
        this.incidentType = incidentType;
        this.incidentValue = incidentValue;
        this.insertionDate = insertionDate;
    }
}
