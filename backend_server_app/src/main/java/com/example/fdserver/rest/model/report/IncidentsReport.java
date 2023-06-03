package com.example.fdserver.rest.model.report;

import com.example.fdserver.rest.model.streams.IncidentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentsReport {
    private List<IncidentDto> incidents;
    private PieChartReportDto pieChartReport;
}
