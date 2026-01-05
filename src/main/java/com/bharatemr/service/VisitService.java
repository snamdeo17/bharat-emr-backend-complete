package com.bharatemr.service;

import com.bharatemr.dto.FollowUpDto;
import com.bharatemr.dto.MedicineDto;
import com.bharatemr.dto.TestDto;
import com.bharatemr.dto.VisitDto;
import com.bharatemr.exception.ResourceNotFoundException;
import com.bharatemr.model.*;
import com.bharatemr.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VisitService {
    
    @Autowired
    private VisitRepository visitRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private PrescriptionRepository prescriptionRepository;
    
    @Autowired
    private FollowUpRepository followUpRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Value("${app.mobile-app.download-link:https://bharatemr.com/download}")
    private String appDownloadLink;
    
    @Transactional
    public VisitDto createVisit(VisitDto visitDto, String doctorId) {
        // Fetch doctor
        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        
        // Fetch patient
        Patient patient = patientRepository.findById(visitDto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        
        // Create visit
        Visit visit = Visit.builder()
                .patient(patient)
                .doctor(doctor)
                .visitDate(LocalDateTime.now())
                .chiefComplaint(visitDto.getChiefComplaint())
                .pastIllness(visitDto.getPastIllness())
                .presentIllness(visitDto.getPresentIllness())
                .medicalHistory(visitDto.getMedicalHistory())
                .surgicalHistory(visitDto.getSurgicalHistory())
                .clinicalNotes(visitDto.getClinicalNotes())
                .build();
        
        Visit savedVisit = visitRepository.save(visit);
        
        // Create prescription if medicines or tests provided
        if ((visitDto.getMedicines() != null && !visitDto.getMedicines().isEmpty()) ||
            (visitDto.getTests() != null && !visitDto.getTests().isEmpty())) {
            
            Prescription prescription = Prescription.builder()
                    .visit(savedVisit)
                    .medicines(new ArrayList<>())
                    .tests(new ArrayList<>())
                    .build();
            
            Prescription savedPrescription = prescriptionRepository.save(prescription);
            
            // Add medicines
            if (visitDto.getMedicines() != null) {
                for (MedicineDto medicineDto : visitDto.getMedicines()) {
                    Medicine medicine = modelMapper.map(medicineDto, Medicine.class);
                    medicine.setPrescription(savedPrescription);
                    savedPrescription.getMedicines().add(medicine);
                }
            }
            
            // Add tests
            if (visitDto.getTests() != null) {
                for (TestDto testDto : visitDto.getTests()) {
                    Test test = modelMapper.map(testDto, Test.class);
                    test.setPrescription(savedPrescription);
                    savedPrescription.getTests().add(test);
                }
            }
            
            prescriptionRepository.save(savedPrescription);
        }
        
        // Create follow-up if scheduled
        if (visitDto.getFollowUp() != null && visitDto.getFollowUp().getScheduledDate() != null) {
            FollowUp followUp = FollowUp.builder()
                    .visit(savedVisit)
                    .patient(patient)
                    .doctor(doctor)
                    .scheduledDate(visitDto.getFollowUp().getScheduledDate())
                    .notes(visitDto.getFollowUp().getNotes())
                    .build();
            
            followUpRepository.save(followUp);
        }
        
        // Send notification to patient
        String summary = visitDto.getChiefComplaint();
        notificationService.sendVisitNotification(
            patient.getMobileNumber(),
            patient.getFullName(),
            doctor.getFullName(),
            summary,
            appDownloadLink,
            patient.getPatientId()
        );
        
        log.info("Visit created: ID={} for patient: {} by doctor: {}", 
            savedVisit.getId(), patient.getPatientId(), doctorId);
        
        return convertToDto(savedVisit);
    }
    
    public VisitDto getVisitById(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));
        
        return convertToDto(visit);
    }
    
    public List<VisitDto> getVisitsByDoctor(String doctorId) {
        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        
        List<Visit> visits = visitRepository.findByDoctorIdOrderByVisitDateDesc(doctor.getId());
        
        return visits.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<VisitDto> getVisitsByPatient(String patientId) {
        Patient patient = patientRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        
        List<Visit> visits = visitRepository.findByPatientIdOrderByVisitDateDesc(patient.getId());
        
        return visits.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public VisitDto updateVisit(Long visitId, VisitDto visitDto) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));
        
        // Update allowed fields
        if (visitDto.getChiefComplaint() != null) {
            visit.setChiefComplaint(visitDto.getChiefComplaint());
        }
        if (visitDto.getPastIllness() != null) {
            visit.setPastIllness(visitDto.getPastIllness());
        }
        if (visitDto.getPresentIllness() != null) {
            visit.setPresentIllness(visitDto.getPresentIllness());
        }
        if (visitDto.getMedicalHistory() != null) {
            visit.setMedicalHistory(visitDto.getMedicalHistory());
        }
        if (visitDto.getSurgicalHistory() != null) {
            visit.setSurgicalHistory(visitDto.getSurgicalHistory());
        }
        if (visitDto.getClinicalNotes() != null) {
            visit.setClinicalNotes(visitDto.getClinicalNotes());
        }
        
        Visit updated = visitRepository.save(visit);
        
        log.info("Visit updated: ID={}", visitId);
        
        return convertToDto(updated);
    }
    
    public List<VisitDto> getVisitsByDateRange(String doctorId, 
                                                LocalDateTime startDate, 
                                                LocalDateTime endDate) {
        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        
        List<Visit> visits = visitRepository.findVisitsByDoctorAndDateRange(
            doctor.getId(), startDate, endDate
        );
        
        return visits.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private VisitDto convertToDto(Visit visit) {
        VisitDto dto = modelMapper.map(visit, VisitDto.class);
        dto.setPatientName(visit.getPatient().getFullName());
        dto.setDoctorName(visit.getDoctor().getFullName());
        dto.setPatientId(visit.getPatient().getId());
        dto.setDoctorId(visit.getDoctor().getId());
        
        // Add prescription data if exists
        prescriptionRepository.findByVisitId(visit.getId()).ifPresent(prescription -> {
            dto.setMedicines(prescription.getMedicines().stream()
                    .map(m -> modelMapper.map(m, MedicineDto.class))
                    .collect(Collectors.toList()));
            dto.setTests(prescription.getTests().stream()
                    .map(t -> modelMapper.map(t, TestDto.class))
                    .collect(Collectors.toList()));
            dto.setPrescriptionPdfUrl(prescription.getPdfUrl());
        });
        
        return dto;
    }
}