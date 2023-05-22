package com.example.fdserver.rest.model.streams;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterFlowDto {

    private Long rackId;
    private Integer litersPerHour;
    private LocalDate insertionDate;
}
