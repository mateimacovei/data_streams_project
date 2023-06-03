package com.example.fdserver.repo.steams;

import com.example.fdserver.model.streams.Incident;
import com.example.fdserver.model.streams.IncidentType;
import com.example.fdserver.service.model.IncidentWithNames;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface IncidentRepo extends JpaRepository<Incident, Long> {

    @Query("select new com.example.fdserver.service.model.IncidentWithNames(i.datacenterId, d.name, r.name,p.label,i.incidentType,i.incidentValue,i.insertionDate) from Incident i " +
            "inner join Datacenter d on d.id=i.datacenterId " +
            "inner join Rack r on r.id=i.rackId " +
            "left join Processor p on p.id=i.processorId " +
            "order by i.insertionDate desc")
    List<IncidentWithNames> findAllByInsertionDate(LocalDateTime from, LocalDateTime to);

    boolean existsIncidentsByProcessorIdAndIncidentTypeAndInsertionDateAfter(Long processorId, IncidentType incidentType, LocalDateTime startOfInterval);

    boolean existsIncidentsByRackIdAndIncidentTypeAndInsertionDateAfter(Long rackId, IncidentType incidentType, LocalDateTime startOfInterval);
}
