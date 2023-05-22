package com.example.fdserver.repo.steams;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fdserver.model.streams.WaterTemperature;

public interface WaterTemperatureRepo extends JpaRepository<WaterTemperature, Long> {
}
