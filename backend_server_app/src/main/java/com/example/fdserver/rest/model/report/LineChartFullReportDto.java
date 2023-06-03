package com.example.fdserver.rest.model.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.IntStream;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineChartFullReportDto {
    private List<Integer> labels;
    private List<LineChartReportDto> datasets;
}
