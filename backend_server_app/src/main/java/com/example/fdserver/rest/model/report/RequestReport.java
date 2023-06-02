package com.example.fdserver.rest.model.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestReport {
    private ValuesType valuesType;
    private Long identifierId;

    private LocalDateTime from;
    private LocalDateTime to;
}
