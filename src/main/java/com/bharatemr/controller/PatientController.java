package com.bharatemr.controller;

import com.bharatemr.dto.*;
import com.bharatemr.service.PatientService;
import com.bharatemr.service.FollowUpService;
import com.bharatemr.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient")
@CrossOrigin(origins = "*")
@Slf4j
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private FollowUpService followUpService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> loginPatient(
            @RequestBody Map<String, String> loginRequest) {
        String mobileNumber = loginRequest.get("mobileNumber");
        String otp = loginRequest.get("otp");

        log.info("Patient login request for: {}", mobileNumber);

        AuthResponseDto response = patientService.loginPatient(mobileNumber, otp);

        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPatientDashboard() {
        String patientId = SecurityUtils.getCurrentUserId();
        log.info("Fetching dashboard for patient: {}", patientId);

        Map<String, Object> dashboard = patientService.getPatientDashboard(patientId);
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    @GetMapping("/visits")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<List<VisitDto>>> getPatientVisits() {
        String patientId = SecurityUtils.getCurrentUserId();
        log.info("Fetching visits for patient: {}", patientId);

        List<VisitDto> visits = patientService.getPatientVisits(patientId);
        return ResponseEntity.ok(ApiResponse.success(visits));
    }

    @GetMapping("/followups/upcoming")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<List<FollowUpDto>>> getUpcomingFollowUps() {
        String patientId = SecurityUtils.getCurrentUserId();
        log.info("Fetching upcoming follow-ups for patient: {}", patientId);

        List<FollowUpDto> followUps = followUpService.getUpcomingFollowUpsByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.success(followUps));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<PatientDto>> getPatientProfile() {
        String patientId = SecurityUtils.getCurrentUserId();
        log.info("Fetching profile for patient: {}", patientId);

        PatientDto patient = patientService.getPatientProfile(patientId);
        return ResponseEntity.ok(ApiResponse.success(patient));
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