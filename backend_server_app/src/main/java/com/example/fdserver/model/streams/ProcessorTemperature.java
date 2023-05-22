package com.example.fdserver.model.streams;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class ProcessorTemperature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(nullable = false, updatable = false)
    private Long processorId;
    @Column(nullable = false, updatable = false)
    private Integer temperature;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime insertionDate;
}
