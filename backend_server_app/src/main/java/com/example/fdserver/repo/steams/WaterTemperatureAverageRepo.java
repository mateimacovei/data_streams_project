package com.example.fdserver.repo.steams;

import com.example.fdserver.model.Datacenter;
import com.example.fdserver.model.streams.WaterTemperature;
import com.example.fdserver.model.streams.WaterTemperatureAverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WaterTemperatureAverageRepo  extends JpaRepository<WaterTemperatureAverage, Long> {

    @Query("select a from WaterTemperatureAverage a " +
            "inner join Rack r on r.id=a.rackId where r.datacenter.id=:datacenterId " +
            "and a.insertionDate > :from and a.insertionDate< :to ")
    List<WaterTemperatureAverage> findByDatacenter(Long datacenterId, LocalDateTime from, LocalDateTime to);
    Optional<WaterTemperatureAverage> findTopByRackIdOrderByInsertionDateDesc(Long rackId);
}
