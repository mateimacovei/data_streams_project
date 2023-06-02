package com.example.fdserver.repo.steams;

import com.example.fdserver.model.streams.Incident;
import com.example.fdserver.model.streams.IncidentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IncidentRepo extends JpaRepository<Incident, Long> {
    List<Incident> findAllByInsertionDateAfter(LocalDateTime startOfInterval);

    boolean existsIncidentsByProcessorIdAndIncidentTypeAndInsertionDateAfter(Long processorId, IncidentType incidentType, LocalDateTime startOfInterval);

    boolean existsIncidentsByRackIdAndIncidentTypeAndInsertionDateAfter(Long rackId, IncidentType incidentType, LocalDateTime startOfInterval);
}
