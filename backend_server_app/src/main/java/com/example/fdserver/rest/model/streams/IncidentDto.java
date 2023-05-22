package com.example.fdserver.rest.model.streams;

import java.time.LocalDate;

import com.example.fdserver.model.streams.IncidentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentDto {

    private Long datacenterId;
    private Long rackId;
    private Long processorId;
    private IncidentType incidentType;
    private String incidentValue;
    private LocalDate insertionDate;
}
