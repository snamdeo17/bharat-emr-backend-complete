package com.bharatemr.controller;

import com.bharatemr.dto.*;
import com.bharatemr.service.DoctorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import com.bharatemr.util.*;

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin(origins = "*")
@Slf4j
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDto>> registerDoctor(
            @Valid @RequestBody DoctorRegistrationDto dto) {
        log.info("Doctor registration request for: {}", dto.getMobileNumber());

        AuthResponseDto response = doctorService.registerDoctor(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Doctor registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> loginDoctor(
            @RequestBody Map<String, String> loginRequest) {
        String mobileNumber = loginRequest.get("mobileNumber");
        String otp = loginRequest.get("otp");

        log.info("Doctor login request for: {}", mobileNumber);

        AuthResponseDto response = doctorService.loginDoctor(mobileNumber, otp);

        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDoctorStats() {
        String doctorId = SecurityUtils.getCurrentUserId();
        log.info("Fetching stats for doctor: {}", doctorId);
        Map<String, Object> stats = doctorService.getDoctorDashboardStats(doctorId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/patients/recent")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<PatientDto>>> getRecentPatients() {
        String doctorId = SecurityUtils.getCurrentUserId();
        log.info("Fetching recent patients for doctor: {}", doctorId);
        List<PatientDto> patients = doctorService.getRecentPatients(doctorId);
        return ResponseEntity.ok(ApiResponse.success(patients));
    }

    @GetMapping("/profile/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<DoctorDto>> getDoctorProfile(
            @PathVariable String doctorId) {
        log.info("Fetching profile for doctor: {}", doctorId);

        DoctorDto doctor = doctorService.getDoctorProfile(doctorId);

        return ResponseEntity.ok(ApiResponse.success(doctor));
    }

    @PutMapping("/profile/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorDto>> updateDoctorProfile(
            @PathVariable String doctorId,
            @Valid @RequestBody DoctorDto dto) {
        log.info("Updating profile for doctor: {}", doctorId);

        DoctorDto updated = doctorService.updateDoctorProfile(doctorId, dto);

        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    @PostMapping("/patients/add")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<PatientDto>> onboardPatient(
            @Valid @RequestBody PatientDto dto) {
        String doctorId = SecurityUtils.getCurrentUserId();
        log.info("Onboarding patient by doctor: {}", doctorId);

        PatientDto patient = doctorService.onboardPatient(doctorId, dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Patient onboarded successfully", patient));
    }

    @GetMapping("/patients")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PatientDto>>> getDoctorPatients() {
        String doctorId = SecurityUtils.getCurrentUserId();
        log.info("Fetching patients for doctor: {}", doctorId);

        List<PatientDto> patients = doctorService.getDoctorPatients(doctorId);

        return ResponseEntity.ok(ApiResponse.success(patients));
    }
}
