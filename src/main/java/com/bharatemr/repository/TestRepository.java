package com.bharatemr.repository;

import com.bharatemr.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    
    List<Test> findByPrescriptionId(Long prescriptionId);
}