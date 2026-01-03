package com.bharatemr.controller;

import com.bharatemr.dto.ApiResponse;
import com.bharatemr.dto.AuthResponseDto;
import com.bharatemr.dto.PatientDto;
import com.bharatemr.dto.VisitDto;
import com.bharatemr.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
@Slf4j
public class PatientController {
    
    @Autowired
    private PatientService patientService;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> loginPatient(
            @RequestBody Map<String, String> loginRequest) {
        String mobileNumber = loginRequest.get("mobileNumber");
        String otp = loginRequest.get("otp");
        
        log.info("Patient login request for: {}", mobileNumber);
        
        AuthResponseDto response = patientService.loginPatient(mobileNumber, otp);
        
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
    
    @GetMapping("/profile/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PatientDto>> getPatientProfile(
            @PathVariable String patientId) {
        log.info("Fetching profile for patient: {}", patientId);
        
        PatientDto patient = patientService.getPatientProfile(patientId);
        
        return ResponseEntity.ok(ApiResponse.success(patient));
    }
    
    @PutMapping("/profile/{patientId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<PatientDto>> updatePatientProfile(
            @PathVariable String patientId,
            @Valid @RequestBody PatientDto dto) {
        log.info("Updating profile for patient: {}", patientId);
        
        PatientDto updated = patientService.updatePatientProfile(patientId, dto);
        
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }
    
    @GetMapping("/dashboard/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPatientDashboard(
            @PathVariable String patientId) {
        log.info("Fetching dashboard for patient: {}", patientId);
        
        Map<String, Object> dashboard = patientService.getPatientDashboard(patientId);
        
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
    
    @GetMapping("/{patientId}/visits")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<VisitDto>>> getPatientVisits(
            @PathVariable String patientId) {
        log.info("Fetching visits for patient: {}", patientId);
        
        List<VisitDto> visits = patientService.getPatientVisits(patientId);
        
        return ResponseEntity.ok(ApiResponse.success(visits));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PatientDto>>> getAllPatients() {
        log.info("Fetching all patients");
        
        List<PatientDto> patients = patientService.getAllPatients();
        
        return ResponseEntity.ok(ApiResponse.success(patients));
    }
    
    @GetMapping("/by-mobile/{mobileNumber}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PatientDto>> getPatientByMobileNumber(
            @PathVariable String mobileNumber) {
        log.info("Fetching patient by mobile: {}", mobileNumber);
        
        PatientDto patient = patientService.getPatientByMobileNumber(mobileNumber);
        
        return ResponseEntity.ok(ApiResponse.success(patient));
    }
}