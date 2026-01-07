package com.bharatemr.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_id", unique = true, nullable = false, length = 50)
    private String doctorId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "mobile_number", unique = true, nullable = false, length = 20)
    private String mobileNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "specialization", nullable = false)
    private String specialization;

    @Column(name = "qualification", nullable = false)
    private String qualification;

    @Column(name = "years_of_experience", nullable = false)
    private Integer yearsOfExperience;

    @Column(name = "clinic_name", nullable = false)
    private String clinicName;

    @Column(name = "clinic_address", nullable = false, columnDefinition = "TEXT")
    private String clinicAddress;

    @Column(name = "medical_registration_number", nullable = false, length = 100)
    private String medicalRegistrationNumber;

    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_blocked")
    @Builder.Default
    private Boolean isBlocked = false;

    @Column(name = "preferred_theme")
    @Builder.Default
    private String preferredTheme = "modern";

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.doctorId == null) {
            this.doctorId = generateDoctorId();
        }
    }

    private String generateDoctorId() {
        String prefix = "DR";
        String namePart = this.fullName != null
                ? this.fullName.replaceAll("\\s+", "").substring(0, Math.min(4, this.fullName.length())).toUpperCase()
                : "USER";
        String randomPart = String.valueOf((int) (Math.random() * 10000));
        return prefix + namePart + randomPart;
    }
}