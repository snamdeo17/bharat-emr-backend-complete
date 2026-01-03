package com.bharatemr.repository;

import com.bharatemr.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    Optional<Patient> findByMobileNumber(String mobileNumber);
    
    Optional<Patient> findByPatientId(String patientId);
    
    List<Patient> findByOnboardedByDoctorId(Long doctorId);
    
    boolean existsByMobileNumber(String mobileNumber);
    
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.isActive = true")
    long countActivePatients();
    
    @Query("SELECT p FROM Patient p WHERE p.onboardedByDoctor.id = :doctorId AND p.isActive = true")
    List<Patient> findActivePatientsByDoctor(@Param("doctorId") Long doctorId);
}