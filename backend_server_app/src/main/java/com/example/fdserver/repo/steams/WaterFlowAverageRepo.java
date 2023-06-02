package com.example.fdserver.repo.steams;

import com.example.fdserver.model.Datacenter;
import com.example.fdserver.model.streams.WaterFlowAverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface WaterFlowAverageRepo  extends JpaRepository<WaterFlowAverage, Long> {
    @Query("select a from WaterFlowAverage a " +
            "inner join Rack r on r.id=a.rackId where r.datacenter.id=:datacenterId " +
            "and a.insertionDate > :from and a.insertionDate< :to ")
    List<WaterFlowAverage> findByDatacenter(Long datacenterId, LocalDateTime from, LocalDateTime to);
}
