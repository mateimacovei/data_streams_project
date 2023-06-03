package com.example.fdserver.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineChartReport {
    private String label;
    // the double represents the timestamp at which the measurement took place
    private List<Map.Entry<Double, Integer>> data;
}
