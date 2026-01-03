package com.bharatemr.repository;

import com.bharatemr.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    
    Optional<Prescription> findByVisitId(Long visitId);
    
    boolean existsByVisitId(Long visitId);
}