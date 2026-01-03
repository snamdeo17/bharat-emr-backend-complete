package com.bharatemr.service;

import com.bharatemr.dto.FollowUpDto;
import com.bharatemr.enums.FollowUpStatus;
import com.bharatemr.exception.ResourceNotFoundException;
import com.bharatemr.model.Doctor;
import com.bharatemr.model.FollowUp;
import com.bharatemr.model.Patient;
import com.bharatemr.model.Visit;
import com.bharatemr.repository.DoctorRepository;
import com.bharatemr.repository.FollowUpRepository;
import com.bharatemr.repository.PatientRepository;
import com.bharatemr.repository.VisitRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FollowUpService {
    
    @Autowired
    private FollowUpRepository followUpRepository;
    
    @Autowired
    private VisitRepository visitRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Transactional
    public FollowUpDto scheduleFollowUp(Long visitId, FollowUpDto dto) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));
        
        FollowUp followUp = FollowUp.builder()
                .visit(visit)
                .patient(visit.getPatient())
                .doctor(visit.getDoctor())
                .scheduledDate(dto.getScheduledDate())
                .notes(dto.getNotes())
                .status(FollowUpStatus.SCHEDULED)
                .build();
        
        FollowUp saved = followUpRepository.save(followUp);
        
        // Send notification
        String formattedDate = dto.getScheduledDate()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        
        notificationService.sendFollowUpReminder(
            visit.getPatient().getMobileNumber(),
            visit.getPatient().getFullName(),
            visit.getDoctor().getFullName(),
            formattedDate
        );
        
        log.info("Follow-up scheduled: ID={} for visit: {}", saved.getId(), visitId);
        
        return convertToDto(saved);
    }
    
    public FollowUpDto getFollowUpById(Long id) {
        FollowUp followUp = followUpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Follow-up not found"));
        
        return convertToDto(followUp);
    }
    
    @Transactional
    public FollowUpDto updateFollowUp(Long id, FollowUpDto dto) {
        FollowUp followUp = followUpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Follow-up not found"));
        
        if (dto.getScheduledDate() != null) {
            followUp.setScheduledDate(dto.getScheduledDate());
        }
        if (dto.getNotes() != null) {
            followUp.setNotes(dto.getNotes());
        }
        if (dto.getStatus() != null) {
            followUp.setStatus(FollowUpStatus.valueOf(dto.getStatus()));
        }
        
        FollowUp updated = followUpRepository.save(followUp);
        
        log.info("Follow-up updated: ID={}", id);
        
        return convertToDto(updated);
    }
    
    @Transactional
    public void cancelFollowUp(Long id) {
        FollowUp followUp = followUpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Follow-up not found"));
        
        followUp.setStatus(FollowUpStatus.CANCELLED);
        followUpRepository.save(followUp);
        
        log.info("Follow-up cancelled: ID={}", id);
    }
    
    @Transactional
    public void completeFollowUp(Long id) {
        FollowUp followUp = followUpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Follow-up not found"));
        
        followUp.setStatus(FollowUpStatus.COMPLETED);
        followUpRepository.save(followUp);
        
        log.info("Follow-up completed: ID={}", id);
    }
    
    public List<FollowUpDto> getFollowUpsByDoctor(String doctorId) {
        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        
        List<FollowUp> followUps = followUpRepository.findByDoctorIdOrderByScheduledDateDesc(doctor.getId());
        
        return followUps.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<FollowUpDto> getFollowUpsByPatient(String patientId) {
        Patient patient = patientRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        
        List<FollowUp> followUps = followUpRepository.findByPatientIdOrderByScheduledDateDesc(patient.getId());
        
        return followUps.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<FollowUpDto> getTodaysFollowUps() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        
        List<FollowUp> followUps = followUpRepository.findByStatusOrderByScheduledDateAsc(FollowUpStatus.SCHEDULED)
                .stream()
                .filter(f -> f.getScheduledDate().isAfter(startOfDay) && f.getScheduledDate().isBefore(endOfDay))
                .collect(Collectors.toList());
        
        return followUps.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Scheduled(cron = "0 0 9 * * *") // Run daily at 9 AM
    public void sendDailyFollowUpReminders() {
        List<FollowUpDto> todaysFollowUps = getTodaysFollowUps();
        
        for (FollowUpDto followUp : todaysFollowUps) {
            try {
                String formattedDate = followUp.getScheduledDate()
                        .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
                
                notificationService.sendFollowUpReminder(
                    followUp.getPatientName(),
                    followUp.getPatientName(),
                    followUp.getDoctorName(),
                    formattedDate
                );
            } catch (Exception e) {
                log.error("Failed to send follow-up reminder for ID: {}", followUp.getId(), e);
            }
        }
        
        log.info("Daily follow-up reminders sent: {} reminders", todaysFollowUps.size());
    }
    
    private FollowUpDto convertToDto(FollowUp followUp) {
        FollowUpDto dto = modelMapper.map(followUp, FollowUpDto.class);
        dto.setPatientName(followUp.getPatient().getFullName());
        dto.setDoctorName(followUp.getDoctor().getFullName());
        dto.setStatus(followUp.getStatus().name());
        return dto;
    }
}