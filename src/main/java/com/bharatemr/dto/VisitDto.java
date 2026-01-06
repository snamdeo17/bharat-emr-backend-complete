package com.bharatemr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitDto {
    
    private Long id;
    
    @NotNull(message = "Patient ID is required")
    private String patientId;
    
    private Long doctorId;
    private String patientName;
    private String doctorName;
    private LocalDateTime visitDate;
    
    @NotBlank(message = "Chief complaint is required")
    private String chiefComplaint;
    
    private String pastIllness;
    private String presentIllness;
    private String medicalHistory;
    private String surgicalHistory;
    private String clinicalNotes;
    
    @Valid
    private List<MedicineDto> medicines;
    
    @Valid
    private List<TestDto> tests;
    
    private FollowUpDto followUp;
    private String prescriptionPdfUrl;
    private LocalDateTime createdAt;
}