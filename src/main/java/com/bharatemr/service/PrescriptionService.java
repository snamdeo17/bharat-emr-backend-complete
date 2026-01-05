package com.bharatemr.service;

import com.bharatemr.dto.MedicineDto;
import com.bharatemr.dto.TestDto;
import com.bharatemr.exception.ResourceNotFoundException;
import com.bharatemr.model.Medicine;
import com.bharatemr.model.Prescription;
import com.bharatemr.model.Test;
import com.bharatemr.model.Visit;
import com.bharatemr.repository.PrescriptionRepository;
import com.bharatemr.repository.VisitRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public Map<String, Object> createPrescription(Long visitId, List<MedicineDto> medicines, List<TestDto> tests) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));

        // Check if prescription already exists
        if (prescriptionRepository.existsByVisitId(visitId)) {
            throw new RuntimeException("Prescription already exists for this visit");
        }

        Prescription prescription = new Prescription();
        prescription.setVisit(visit);

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        // Add medicines
        if (medicines != null && !medicines.isEmpty()) {
            for (MedicineDto medicineDto : medicines) {
                Medicine medicine = modelMapper.map(medicineDto, Medicine.class);
                medicine.setPrescription(savedPrescription);
                savedPrescription.addMedicine(medicine);
            }
        }

        // Add tests
        if (tests != null && !tests.isEmpty()) {
            for (TestDto testDto : tests) {
                Test test = modelMapper.map(testDto, Test.class);
                test.setPrescription(savedPrescription);
                savedPrescription.addTest(test);
            }
        }

        prescriptionRepository.save(savedPrescription);

        log.info("Prescription created for visit: {}", visitId);

        Map<String, Object> response = new HashMap<>();
        response.put("prescriptionId", savedPrescription.getId());
        response.put("visitId", visitId);
        response.put("medicines", medicines);
        response.put("tests", tests);

        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPrescriptionByVisit(Long visitId) {
        Prescription prescription = prescriptionRepository.findByVisitId(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found for this visit"));

        Map<String, Object> response = new HashMap<>();
        response.put("prescriptionId", prescription.getId());
        response.put("visitId", visitId);
        response.put("medicines", prescription.getMedicines().stream()
                .map(m -> modelMapper.map(m, MedicineDto.class))
                .collect(Collectors.toList()));
        response.put("tests", prescription.getTests().stream()
                .map(t -> modelMapper.map(t, TestDto.class))
                .collect(Collectors.toList()));
        response.put("pdfUrl", prescription.getPdfUrl());
        response.put("createdAt", prescription.getCreatedAt());

        return response;
    }

    @Transactional
    public String generatePrescriptionPdf(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));

        Prescription prescription = prescriptionRepository.findByVisitId(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));

        try {
            String pdfUrl = pdfGeneratorService.generatePrescriptionPdf(visit, prescription);
            prescription.setPdfUrl(pdfUrl);
            prescriptionRepository.save(prescription);

            log.info("Prescription PDF generated for visit: {}", visitId);
            return pdfUrl;
        } catch (Exception e) {
            log.error("Failed to generate prescription PDF for visit: {}", visitId, e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public byte[] downloadPrescriptionPdf(Long visitId) {
        Prescription prescription = prescriptionRepository.findByVisitId(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));

        if (prescription.getPdfUrl() == null) {
            throw new ResourceNotFoundException("PDF not generated yet");
        }

        try {
            return pdfGeneratorService.readPdfFile(prescription.getPdfUrl());
        } catch (Exception e) {
            log.error("Failed to read prescription PDF", e);
            throw new RuntimeException("Failed to download PDF: " + e.getMessage());
        }
    }
}