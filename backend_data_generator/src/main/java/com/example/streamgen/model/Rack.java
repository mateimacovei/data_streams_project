package com.example.streamgen.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rack {

    private Long id;
    private String name;
    private Datacenter datacenter;
    private List<Processor> processors;

    private Integer waterTemperatureMin;
    private Integer waterTemperatureMax;

    private Integer waterFlowMin;
    private Integer waterFlowMax;
}
