package com.bharatemr.service;

import com.bharatemr.dto.AuthResponseDto;
import com.bharatemr.dto.DoctorDto;
import com.bharatemr.dto.DoctorRegistrationDto;
import com.bharatemr.dto.PatientDto;
import com.bharatemr.enums.OtpPurpose;
import com.bharatemr.exception.DuplicateResourceException;
import com.bharatemr.exception.ResourceNotFoundException;
import com.bharatemr.model.Doctor;
import com.bharatemr.model.Patient;
import com.bharatemr.repository.DoctorRepository;
import com.bharatemr.repository.PatientRepository;
import com.bharatemr.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DoctorService {
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private OtpService otpService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Transactional
    public AuthResponseDto registerDoctor(DoctorRegistrationDto dto) {
        // Verify OTP first
        boolean otpValid = otpService.verifyOtp(
            dto.getMobileNumber(), 
            dto.getOtp(), 
            OtpPurpose.REGISTRATION
        );
        
        if (!otpValid) {
            throw new RuntimeException("Invalid OTP");
        }
        
        // Check if mobile number already exists
        if (doctorRepository.existsByMobileNumber(dto.getMobileNumber())) {
            throw new DuplicateResourceException("Doctor with this mobile number already exists");
        }
        
        // Check if email already exists (if provided)
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            if (doctorRepository.existsByEmail(dto.getEmail())) {
                throw new DuplicateResourceException("Doctor with this email already exists");
            }
        }
        
        // Create doctor entity
        Doctor doctor = modelMapper.map(dto, Doctor.class);
        doctor.setIsActive(true);
        doctor.setIsBlocked(false);
        
        // Save doctor
        Doctor savedDoctor = doctorRepository.save(doctor);
        
        log.info("Doctor registered successfully: {} with ID: {}", 
            savedDoctor.getFullName(), savedDoctor.getDoctorId());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(
            savedDoctor.getMobileNumber(),
            "DOCTOR",
            savedDoctor.getDoctorId()
        );
        
        String refreshToken = jwtUtil.generateRefreshToken(savedDoctor.getMobileNumber());
        
        return AuthResponseDto.builder()
                .token(token)
                .refreshToken(refreshToken)
                .userId(savedDoctor.getDoctorId())
                .userName(savedDoctor.getFullName())
                .userType("DOCTOR")
                .expiresIn(86400000L)
                .build();
    }
    
    @Transactional
    public AuthResponseDto loginDoctor(String mobileNumber, String otp) {
        // Verify OTP
        boolean otpValid = otpService.verifyOtp(
            mobileNumber, 
            otp, 
            OtpPurpose.LOGIN
        );
        
        if (!otpValid) {
            throw new RuntimeException("Invalid OTP");
        }
        
        // Find doctor
        Doctor doctor = doctorRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        
        if (!doctor.getIsActive() || doctor.getIsBlocked()) {
            throw new RuntimeException("Account is inactive or blocked");
        }
        
        // Generate JWT token
        String token = jwtUtil.generateToken(
            doctor.getMobileNumber(),
            "DOCTOR",
            doctor.getDoctorId()
        );
        
        String refreshToken = jwtUtil.generateRefreshToken(doctor.getMobileNumber());
        
        log.info("Doctor logged in: {}", doctor.getDoctorId());
        
        return AuthResponseDto.builder()
                .token(token)
                .refreshToken(refreshToken)
                .userId(doctor.getDoctorId())
                .userName(doctor.getFullName())
                .userType("DOCTOR")
                .expiresIn(86400000L)
                .build();
    }
    
    public DoctorDto getDoctorProfile(String doctorId) {
        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        
        return modelMapper.map(doctor, DoctorDto.class);
    }
    
    @Transactional
    public DoctorDto updateDoctorProfile(String doctorId, DoctorDto dto) {
        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        
        // Update allowed fields
        if (dto.getEmail() != null) doctor.setEmail(dto.getEmail());
        if (dto.getSpecialization() != null) doctor.setSpecialization(dto.getSpecialization());
        if (dto.getQualification() != null) doctor.setQualification(dto.getQualification());
        if (dto.getYearsOfExperience() != null) doctor.setYearsOfExperience(dto.getYearsOfExperience());
        if (dto.getClinicName() != null) doctor.setClinicName(dto.getClinicName());
        if (dto.getClinicAddress() != null) doctor.setClinicAddress(dto.getClinicAddress());
        if (dto.getProfilePhotoUrl() != null) doctor.setProfilePhotoUrl(dto.getProfilePhotoUrl());
        
        Doctor updated = doctorRepository.save(doctor);
        
        log.info("Doctor profile updated: {}", doctorId);
        
        return modelMapper.map(updated, DoctorDto.class);
    }
    
    @Transactional
    public PatientDto onboardPatient(String doctorId, PatientDto dto) {
        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        
        // Create patient
        Patient patient = modelMapper.map(dto, Patient.class);
        patient.setOnboardedByDoctor(doctor);
        patient.setIsActive(true);
        patient.setIsBlocked(false);
        
        Patient savedPatient = patientRepository.save(patient);
        
        log.info("Patient onboarded: {} by doctor: {}", 
            savedPatient.getPatientId(), doctorId);
        
        PatientDto result = modelMapper.map(savedPatient, PatientDto.class);
        result.setOnboardedByDoctorName(doctor.getFullName());
        result.setOnboardedByDoctorId_str(doctor.getDoctorId());
        
        return result;
    }
    
    public List<PatientDto> getDoctorPatients(String doctorId) {
        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        
        List<Patient> patients = patientRepository.findActivePatientsByDoctor(doctor.getId());
        
        return patients.stream()
                .map(patient -> {
                    PatientDto dto = modelMapper.map(patient, PatientDto.class);
                    dto.setOnboardedByDoctorName(doctor.getFullName());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    public List<DoctorDto> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(doctor -> modelMapper.map(doctor, DoctorDto.class))
                .collect(Collectors.toList());
    }
    
    public long getActiveDoctorsCount() {
        return doctorRepository.countActiveDoctors();
    }
}