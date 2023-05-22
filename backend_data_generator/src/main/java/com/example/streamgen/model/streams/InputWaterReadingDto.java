package com.example.streamgen.model.streams;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputWaterReadingDto {

    private Long rackId;
    private Integer temperature;
    private Integer litersPerHour;
}
