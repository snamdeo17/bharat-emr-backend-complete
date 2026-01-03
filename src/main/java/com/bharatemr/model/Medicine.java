package com.bharatemr.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescription_medicines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Medicine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;
    
    @Column(name = "medicine_name", nullable = false)
    private String medicineName;
    
    @Column(name = "dosage", nullable = false, length = 100)
    private String dosage;
    
    @Column(name = "frequency", nullable = false, length = 100)
    private String frequency;
    
    @Column(name = "duration", nullable = false, length = 100)
    private String duration;
    
    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}