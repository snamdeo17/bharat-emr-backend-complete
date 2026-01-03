package com.bharatemr.controller;

import com.bharatemr.dto.ApiResponse;
import com.bharatemr.dto.DoctorDto;
import com.bharatemr.dto.PatientDto;
import com.bharatemr.service.DoctorService;
import com.bharatemr.service.PatientService;
import com.bharatemr.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminController {
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private VisitRepository visitRepository;
    
    @Autowired
    private FollowUpRepository followUpRepository;
    
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        log.info("Fetching admin dashboard statistics");
        
        Map<String, Object> dashboard = new HashMap<>();
        
        // Total counts
        dashboard.put("totalDoctors", doctorRepository.count());
        dashboard.put("activeDoctors", doctorService.getActiveDoctorsCount());
        dashboard.put("totalPatients", patientRepository.count());
        dashboard.put("activePatients", patientService.getActivePatientsCount());
        dashboard.put("totalVisits", visitRepository.count());
        
        // Today's statistics
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        
        long todaysVisits = visitRepository.countVisitsByDateRange(startOfDay, endOfDay);
        dashboard.put("todaysVisits", todaysVisits);
        dashboard.put("todaysFollowUps", followUpRepository.countTodaysScheduledFollowUps());
        
        // Recent registrations
        LocalDateTime last7Days = LocalDateTime.now().minusDays(7);
        long newDoctorsLast7Days = doctorRepository.findAll().stream()
                .filter(d -> d.getCreatedAt().isAfter(last7Days))
                .count();
        long newPatientsLast7Days = patientRepository.findAll().stream()
                .filter(p -> p.getCreatedAt().isAfter(last7Days))
                .count();
        
        dashboard.put("newDoctorsLast7Days", newDoctorsLast7Days);
        dashboard.put("newPatientsLast7Days", newPatientsLast7Days);
        
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
    
    @GetMapping("/doctors")
    public ResponseEntity<ApiResponse<List<DoctorDto>>> getAllDoctors() {
        log.info("Admin fetching all doctors");
        
        List<DoctorDto> doctors = doctorService.getAllDoctors();
        
        return ResponseEntity.ok(ApiResponse.success(doctors));
    }
    
    @GetMapping("/patients")
    public ResponseEntity<ApiResponse<List<PatientDto>>> getAllPatients() {
        log.info("Admin fetching all patients");
        
        List<PatientDto> patients = patientService.getAllPatients();
        
        return ResponseEntity.ok(ApiResponse.success(patients));
    }
    
    @PutMapping("/doctors/{doctorId}/block")
    public ResponseEntity<ApiResponse<String>> toggleDoctorBlock(
            @PathVariable String doctorId,
            @RequestParam boolean block) {
        log.info("Admin {} doctor: {}", block ? "blocking" : "unblocking", doctorId);
        
        var doctor = doctorRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        doctor.setIsBlocked(block);
        doctorRepository.save(doctor);
        
        String message = block ? "Doctor blocked successfully" : "Doctor unblocked successfully";
        
        return ResponseEntity.ok(ApiResponse.success(message, doctorId));
    }
    
    @PutMapping("/patients/{patientId}/block")
    public ResponseEntity<ApiResponse<String>> togglePatientBlock(
            @PathVariable String patientId,
            @RequestParam boolean block) {
        log.info("Admin {} patient: {}", block ? "blocking" : "unblocking", patientId);
        
        var patient = patientRepository.findByPatientId(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        patient.setIsBlocked(block);
        patientRepository.save(patient);
        
        String message = block ? "Patient blocked successfully" : "Patient unblocked successfully";
        
        return ResponseEntity.ok(ApiResponse.success(message, patientId));
    }
    
    @GetMapping("/statistics/monthly")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMonthlyStatistics() {
        log.info("Fetching monthly statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime now = LocalDateTime.now();
        
        long monthlyVisits = visitRepository.countVisitsByDateRange(startOfMonth, now);
        stats.put("monthlyVisits", monthlyVisits);
        
        // Add more monthly statistics as needed
        stats.put("currentMonth", now.getMonth().name());
        stats.put("year", now.getYear());
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}