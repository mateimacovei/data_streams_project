package com.example.fdserver.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fdserver.model.Datacenter;

public interface DatacenterRepo extends JpaRepository<Datacenter, Long> {
}
