package com.example.fdserver.repo.steams;

import com.example.fdserver.model.streams.WaterTemperature;
import com.example.fdserver.model.streams.WaterTemperatureAverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface WaterTemperatureRepo extends JpaRepository<WaterTemperature, Long> {

    @Query("select a from WaterTemperature a " +
            "inner join Rack r on r.id=a.rackId where r.datacenter.id=:datacenterId " +
            "and a.insertionDate > :from and a.insertionDate< :to ")
    List<WaterTemperature> findByDatacenter(Long datacenterId, LocalDateTime from, LocalDateTime to);
    List<WaterTemperature> findAllByRackIdAndInsertionDateAfter(Long rackId, LocalDateTime startOfInterval);
    List<WaterTemperature> findAllByInsertionDateAfter(LocalDateTime startOfInterval);
}
