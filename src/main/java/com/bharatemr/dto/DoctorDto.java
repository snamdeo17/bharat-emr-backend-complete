package com.bharatemr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {
    private Long id;
    private String doctorId;
    private String fullName;
    private String mobileNumber;
    private String email;
    private String specialization;
    private String qualification;
    private Integer yearsOfExperience;
    private String clinicName;
    private String clinicAddress;
    private String medicalRegistrationNumber;
    private String profilePhotoUrl;
    private Boolean isActive;
    private Boolean isBlocked;
    private String preferredTheme;
    private LocalDateTime createdAt;
}