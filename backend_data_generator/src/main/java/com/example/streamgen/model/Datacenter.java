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
public class Datacenter {

    private Long id;
    private String name;
    private List<Rack> racks;
}
