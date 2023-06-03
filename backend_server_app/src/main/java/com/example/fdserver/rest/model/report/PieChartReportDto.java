package com.example.fdserver.rest.model.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PieChartReportDto {
    private List<String> labels;
    private List<Map.Entry<String, List<Integer>>> values;
}
