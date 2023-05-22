package com.example.fdserver.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Rack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(nullable = false, updatable = false)
    private Datacenter datacenter;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rack", orphanRemoval = true)
    private List<Processor> processors;
}
