package com.bharatemr.repository;

import com.bharatemr.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    Optional<Doctor> findByMobileNumber(String mobileNumber);
    
    Optional<Doctor> findByDoctorId(String doctorId);
    
    boolean existsByMobileNumber(String mobileNumber);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.isActive = true")
    long countActiveDoctors();
    
    @Query("SELECT d FROM Doctor d WHERE d.isActive = true AND d.isBlocked = false")
    java.util.List<Doctor> findAllActiveAndNotBlocked();
}