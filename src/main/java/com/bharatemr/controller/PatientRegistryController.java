package com.bharatemr.controller;

import com.bharatemr.dto.ApiResponse;
import com.bharatemr.dto.PaginatedResponse;
import com.bharatemr.dto.PatientDto;
import com.bharatemr.dto.DoctorDto;
import com.bharatemr.service.PatientService;
import com.bharatemr.service.DoctorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
@Slf4j
public class PatientRegistryController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<PatientDto>>> getPatients(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo) {

        log.info("Fetching paginated patients. Page: {}, Size: {}, Search: {}", page, size, search);

        PaginatedResponse<PatientDto> response = patientService.getPaginatedPatients(
                page, size, sortBy, sortDir, search, gender, minAge, maxAge, doctorId, isActive, createdFrom,
                createdTo);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/doctors")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<DoctorDto>>> getAllDoctors() {
        return ResponseEntity.ok(ApiResponse.success(doctorService.getAllDoctors()));
    }
}
