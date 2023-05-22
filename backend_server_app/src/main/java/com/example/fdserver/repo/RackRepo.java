package com.example.fdserver.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fdserver.model.Rack;

public interface RackRepo extends JpaRepository<Rack, Long> {

    List<Rack> findAllByDatacenter_Id(Long datacenterId);
}
