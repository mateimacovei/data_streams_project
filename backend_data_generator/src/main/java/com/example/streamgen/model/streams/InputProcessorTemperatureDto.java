package com.example.streamgen.model.streams;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputProcessorTemperatureDto {

    private Long processorId;
    private Integer temperature;
}
