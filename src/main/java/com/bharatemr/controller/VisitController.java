package com.bharatemr.controller;

import com.bharatemr.dto.ApiResponse;
import com.bharatemr.dto.VisitDto;
import com.bharatemr.service.PrescriptionService;
import com.bharatemr.service.VisitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import com.bharatemr.dto.PaginatedResponse;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/visits")
@CrossOrigin(origins = "*")
@Slf4j
public class VisitController {

    @Autowired
    private VisitService visitService;

    @Autowired
    private PrescriptionService prescriptionService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<VisitDto>> createVisit(
            @Valid @RequestBody VisitDto visitDto,
            Authentication authentication) {
        // Extract doctor ID from JWT token (stored in authentication principal)
        String doctorId = authentication.getName();

        log.info("Creating visit for patient: {} by doctor: {}",
                visitDto.getPatientId(), doctorId);

        VisitDto created = visitService.createVisit(visitDto, doctorId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Visit created successfully", created));
    }

    @GetMapping("/{visitId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<VisitDto>> getVisitById(
            @PathVariable Long visitId) {
        log.info("Fetching visit: {}", visitId);

        VisitDto visit = visitService.getVisitById(visitId);

        return ResponseEntity.ok(ApiResponse.success(visit));
    }

    @PutMapping("/{visitId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<VisitDto>> updateVisit(
            @PathVariable Long visitId,
            @Valid @RequestBody VisitDto visitDto) {
        log.info("Updating visit: {}", visitId);

        VisitDto updated = visitService.updateVisit(visitId, visitDto);

        return ResponseEntity.ok(ApiResponse.success("Visit updated successfully", updated));
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<VisitDto>>> getVisitsByDoctor(
            @PathVariable String doctorId) {
        log.info("Fetching visits for doctor: {}", doctorId);

        List<VisitDto> visits = visitService.getVisitsByDoctor(doctorId);

        return ResponseEntity.ok(ApiResponse.success(visits));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<VisitDto>>> getVisitsByPatient(
            @PathVariable String patientId) {
        log.info("Fetching visits for patient: {}", patientId);

        List<VisitDto> visits = visitService.getVisitsByPatient(patientId);

        return ResponseEntity.ok(ApiResponse.success(visits));
    }

    @GetMapping("/doctor/{doctorId}/date-range")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<VisitDto>>> getVisitsByDateRange(
            @PathVariable String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching visits for doctor: {} from {} to {}", doctorId, startDate, endDate);

        List<VisitDto> visits = visitService.getVisitsByDateRange(doctorId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(visits));
    }

    @GetMapping("/{visitId}/prescription/pdf")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT', 'ADMIN')")
    public ResponseEntity<byte[]> downloadPrescriptionPdf(
            @PathVariable Long visitId) {
        log.info("Downloading prescription PDF for visit: {}", visitId);

        try {
            byte[] pdfContent = prescriptionService.downloadPrescriptionPdf(visitId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "prescription_" + visitId + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent);
        } catch (Exception e) {
            log.error("Error downloading prescription PDF", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{visitId}/prescription/generate-pdf")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<String>> generatePrescriptionPdf(
            @PathVariable Long visitId) {
        log.info("Generating prescription PDF for visit: {}", visitId);

        String pdfUrl = prescriptionService.generatePrescriptionPdf(visitId);

        return ResponseEntity.ok(ApiResponse.success("PDF generated successfully", pdfUrl));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<VisitDto>>> getVisits(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "visitDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime visitFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime visitTo) {

        log.info("Fetching paginated visits. Page: {}, Size: {}, Search: {}", page, size, search);

        PaginatedResponse<VisitDto> response = visitService.getPaginatedVisits(
                page, size, sortBy, sortDir, search, doctorId, visitFrom, visitTo);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}