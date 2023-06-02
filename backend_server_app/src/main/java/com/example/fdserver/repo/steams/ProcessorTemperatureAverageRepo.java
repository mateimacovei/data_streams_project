package com.example.fdserver.repo.steams;

import com.example.fdserver.model.Datacenter;
import com.example.fdserver.model.streams.ProcessorTemperature;
import com.example.fdserver.model.streams.ProcessorTemperatureAverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProcessorTemperatureAverageRepo extends JpaRepository<ProcessorTemperatureAverage, Long> {

    @Query("select a from ProcessorTemperatureAverage a " +
            "inner join Processor p on p.id=a.processorId where p.rack.id=:rackId " +
            "and a.insertionDate > :from and a.insertionDate< :to ")
    List<ProcessorTemperatureAverage> findByRack(Long rackId, LocalDateTime from, LocalDateTime to);

    Optional<ProcessorTemperatureAverage> findTopByProcessorIdOrderByInsertionDateDesc(Long processorId);
}
