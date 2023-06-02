package com.example.fdserver.repo.steams;

import com.example.fdserver.model.streams.WaterFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface WaterFlowRepo extends JpaRepository<WaterFlow, Long> {

    @Query("select a from WaterFlow a " +
            "inner join Rack r on r.id=a.rackId where r.datacenter.id=:datacenterId " +
            "and a.insertionDate > :from and a.insertionDate< :to ")
    List<WaterFlow> findByDatacenter(Long datacenterId, LocalDateTime from, LocalDateTime to);
    List<WaterFlow> findAllByRackIdAndInsertionDateAfter(Long rackId, LocalDateTime startOfInterval);
    List<WaterFlow> findAllByInsertionDateAfter(LocalDateTime startOfInterval);
}

