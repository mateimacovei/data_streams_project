package com.example.fdserver.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fdserver.model.Processor;

public interface ProcessorRepo extends JpaRepository<Processor, Long> {
}
