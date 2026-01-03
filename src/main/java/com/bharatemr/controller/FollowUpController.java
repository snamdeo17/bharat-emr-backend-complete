package com.bharatemr.controller;

import com.bharatemr.dto.ApiResponse;
import com.bharatemr.dto.FollowUpDto;
import com.bharatemr.service.FollowUpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/follow-ups")
@CrossOrigin(origins = "*")
@Slf4j
public class FollowUpController {
    
    @Autowired
    private FollowUpService followUpService;
    
    @PostMapping("/visit/{visitId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<FollowUpDto>> scheduleFollowUp(
            @PathVariable Long visitId,
            @Valid @RequestBody FollowUpDto dto) {
        log.info("Scheduling follow-up for visit: {}", visitId);
        
        FollowUpDto followUp = followUpService.scheduleFollowUp(visitId, dto);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Follow-up scheduled successfully", followUp));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<FollowUpDto>> getFollowUpById(
            @PathVariable Long id) {
        log.info("Fetching follow-up: {}", id);
        
        FollowUpDto followUp = followUpService.getFollowUpById(id);
        
        return ResponseEntity.ok(ApiResponse.success(followUp));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<FollowUpDto>> updateFollowUp(
            @PathVariable Long id,
            @Valid @RequestBody FollowUpDto dto) {
        log.info("Updating follow-up: {}", id);
        
        FollowUpDto updated = followUpService.updateFollowUp(id, dto);
        
        return ResponseEntity.ok(ApiResponse.success("Follow-up updated successfully", updated));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Void>> cancelFollowUp(
            @PathVariable Long id) {
        log.info("Cancelling follow-up: {}", id);
        
        followUpService.cancelFollowUp(id);
        
        return ResponseEntity.ok(ApiResponse.success("Follow-up cancelled successfully", null));
    }
    
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Void>> completeFollowUp(
            @PathVariable Long id) {
        log.info("Marking follow-up as completed: {}", id);
        
        followUpService.completeFollowUp(id);
        
        return ResponseEntity.ok(ApiResponse.success("Follow-up marked as completed", null));
    }
    
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<FollowUpDto>>> getFollowUpsByDoctor(
            @PathVariable String doctorId) {
        log.info("Fetching follow-ups for doctor: {}", doctorId);
        
        List<FollowUpDto> followUps = followUpService.getFollowUpsByDoctor(doctorId);
        
        return ResponseEntity.ok(ApiResponse.success(followUps));
    }
    
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<FollowUpDto>>> getFollowUpsByPatient(
            @PathVariable String patientId) {
        log.info("Fetching follow-ups for patient: {}", patientId);
        
        List<FollowUpDto> followUps = followUpService.getFollowUpsByPatient(patientId);
        
        return ResponseEntity.ok(ApiResponse.success(followUps));
    }
    
    @GetMapping("/today")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<FollowUpDto>>> getTodaysFollowUps() {
        log.info("Fetching today's follow-ups");
        
        List<FollowUpDto> followUps = followUpService.getTodaysFollowUps();
        
        return ResponseEntity.ok(ApiResponse.success(followUps));
    }
}