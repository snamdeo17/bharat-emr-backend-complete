package com.bharatemr.service;

import com.bharatemr.dto.AuthResponseDto;
import com.bharatemr.dto.PatientDto;
import com.bharatemr.dto.VisitDto;
import com.bharatemr.enums.OtpPurpose;
import com.bharatemr.exception.ResourceNotFoundException;
import com.bharatemr.model.Patient;
import com.bharatemr.model.Visit;
import com.bharatemr.repository.PatientRepository;
import com.bharatemr.repository.VisitRepository;
import com.bharatemr.security.JwtUtil;
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
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public AuthResponseDto loginPatient(String mobileNumber, String otp) {
        // Verify OTP
        boolean otpValid = otpService.verifyOtp(
                mobileNumber,
                otp,
                OtpPurpose.LOGIN);

        if (!otpValid) {
            throw new RuntimeException("Invalid OTP");
        }

        // Find patient
        Patient patient = patientRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found. Please contact your doctor to register."));

        if (!patient.getIsActive() || patient.getIsBlocked()) {
            throw new RuntimeException("Account is inactive or blocked");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(
                patient.getMobileNumber(),
                "PATIENT",
                patient.getPatientId());

        String refreshToken = jwtUtil.generateRefreshToken(patient.getMobileNumber());

        log.info("Patient logged in: {}", patient.getPatientId());

        return AuthResponseDto.builder()
                .token(token)
                .refreshToken(refreshToken)
                .userId(patient.getPatientId())
                .name(patient.getFullName())
                .userType("PATIENT")
                .expiresIn(86400000L)
                .build();
    }

    @Transactional(readOnly = true)
    public PatientDto getPatientProfile(String patientId) {
        Patient patient = patientRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        PatientDto dto = modelMapper.map(patient, PatientDto.class);
        dto.setOnboardedByDoctorName(patient.getOnboardedByDoctor().getFullName());
        dto.setOnboardedByDoctorId_str(patient.getOnboardedByDoctor().getDoctorId());

        return dto;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPatientDashboard(String patientId) {
        Patient patient = patientRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        List<Visit> visits = visitRepository.findByPatientIdOrderByVisitDateDesc(patient.getId());

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("patientInfo", modelMapper.map(patient, PatientDto.class));
        dashboard.put("totalVisits", visits.size());
        dashboard.put("lastVisitDate", visits.isEmpty() ? null : visits.get(0).getVisitDate());
        dashboard.put("onboardedByDoctor", patient.getOnboardedByDoctor().getFullName());
        dashboard.put("recentVisits", visits.stream()
                .limit(5)
                .map(visit -> {
                    Map<String, Object> visitInfo = new HashMap<>();
                    visitInfo.put("id", visit.getId());
                    visitInfo.put("date", visit.getVisitDate());
                    visitInfo.put("doctorName", visit.getDoctor().getFullName());
                    visitInfo.put("chiefComplaint", visit.getChiefComplaint());
                    return visitInfo;
                })
                .collect(Collectors.toList()));

        log.info("Dashboard data retrieved for patient: {}", patientId);

        return dashboard;
    }

    @Transactional(readOnly = true)
    public List<VisitDto> getPatientVisits(String patientId) {
        Patient patient = patientRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        List<Visit> visits = visitRepository.findByPatientIdOrderByVisitDateDesc(patient.getId());

        return visits.stream()
                .map(visit -> {
                    VisitDto dto = modelMapper.map(visit, VisitDto.class);
                    dto.setPatientName(patient.getFullName());
                    dto.setDoctorName(visit.getDoctor().getFullName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public PatientDto updatePatientProfile(String patientId, PatientDto dto) {
        Patient patient = patientRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        // Update allowed fields
        if (dto.getEmail() != null)
            patient.setEmail(dto.getEmail());
        if (dto.getAddress() != null)
            patient.setAddress(dto.getAddress());

        Patient updated = patientRepository.save(patient);

        log.info("Patient profile updated: {}", patientId);

        return modelMapper.map(updated, PatientDto.class);
    }

    @Transactional(readOnly = true)
    public PatientDto getPatientByMobileNumber(String mobileNumber) {
        Patient patient = patientRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        return modelMapper.map(patient, PatientDto.class);
    }

    @Transactional(readOnly = true)
    public List<PatientDto> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(patient -> modelMapper.map(patient, PatientDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getActivePatientsCount() {
        return patientRepository.countActivePatients();
    }
}