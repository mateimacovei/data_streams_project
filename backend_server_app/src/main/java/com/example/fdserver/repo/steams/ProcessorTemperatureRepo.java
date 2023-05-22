package com.example.fdserver.repo.steams;

import com.example.fdserver.model.Datacenter;
import com.example.fdserver.model.streams.ProcessorTemperature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessorTemperatureRepo  extends JpaRepository<ProcessorTemperature, Long> {
}
