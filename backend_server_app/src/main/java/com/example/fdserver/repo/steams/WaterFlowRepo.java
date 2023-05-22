package com.example.fdserver.repo.steams;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fdserver.model.streams.WaterFlow;

public interface WaterFlowRepo extends JpaRepository<WaterFlow, Long> {
}
