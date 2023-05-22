package com.example.fdserver.repo.steams;

import com.example.fdserver.model.Datacenter;
import com.example.fdserver.model.streams.WaterTemperatureAverage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaterTemperatureAverageRepo  extends JpaRepository<WaterTemperatureAverage, Long> {
}
