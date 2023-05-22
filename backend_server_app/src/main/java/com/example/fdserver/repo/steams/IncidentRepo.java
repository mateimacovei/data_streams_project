package com.example.fdserver.repo.steams;

import com.example.fdserver.model.Datacenter;
import com.example.fdserver.model.streams.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepo  extends JpaRepository<Incident, Long> {
}
