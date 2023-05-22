package com.example.fdserver.repo.steams;

import com.example.fdserver.model.Datacenter;
import com.example.fdserver.model.streams.WaterFlowAverage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaterFlowAverageRepo  extends JpaRepository<WaterFlowAverage, Long> {
}
