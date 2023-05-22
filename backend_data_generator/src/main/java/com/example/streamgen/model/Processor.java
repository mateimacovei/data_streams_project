package com.example.streamgen.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Processor {

    private Long id;
    private String label;

    private Integer temperatureMin;
    private Integer temperatureMax;
}
