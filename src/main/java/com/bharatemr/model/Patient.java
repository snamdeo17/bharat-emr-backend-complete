package com.bharatemr.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Patient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "patient_id", unique = true, nullable = false, length = 50)
    private String patientId;
    
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    @Column(name = "gender", nullable = false, length = 20)
    private String gender;
    
    @Column(name = "age", nullable = false)
    private Integer age;
    
    @Column(name = "mobile_number", nullable = false, length = 20)
    private String mobileNumber;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onboarded_by_doctor_id", nullable = false)
    private Doctor onboardedByDoctor;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "is_blocked")
    @Builder.Default
    private Boolean isBlocked = false;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist() {
        if (this.patientId == null) {
            this.patientId = generatePatientId();
        }
    }
    
    private String generatePatientId() {
        String prefix = "PT";
        String namePart = this.fullName != null ? 
            this.fullName.replaceAll("\\s+", "").substring(0, Math.min(4, this.fullName.length())).toUpperCase() : 
            "USER";
        String randomPart = String.valueOf((int)(Math.random() * 10000));
        return prefix + namePart + randomPart;
    }
}