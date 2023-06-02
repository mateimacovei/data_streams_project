package com.example.fdserver.repo.steams;

import com.example.fdserver.model.Datacenter;
import com.example.fdserver.model.streams.ProcessorTemperature;
import com.example.fdserver.model.streams.ProcessorTemperatureAverage;
import com.example.fdserver.model.streams.WaterTemperature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ProcessorTemperatureRepo  extends JpaRepository<ProcessorTemperature, Long> {

    @Query("select a from ProcessorTemperature a " +
            "inner join Processor p on p.id=a.processorId where p.rack.id=:rackId " +
            "and a.insertionDate > :from and a.insertionDate< :to ")
    List<ProcessorTemperature> findByRack(Long rackId, LocalDateTime from, LocalDateTime to);
    List<ProcessorTemperature> findAllByProcessorIdAndInsertionDateAfter(Long processorId, LocalDateTime startOfInterval);
    List<ProcessorTemperature> findAllByInsertionDateAfter(LocalDateTime startOfInterval);
}
