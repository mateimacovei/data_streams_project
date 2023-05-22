package com.example.fdserver.repo.steams;

import com.example.fdserver.model.Datacenter;
import com.example.fdserver.model.streams.ProcessorTemperatureAverage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessorTemperatureAverageRepo  extends JpaRepository<ProcessorTemperatureAverage, Long> {
}
