package com.bharatemr.repository;

import com.bharatemr.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findByPatientIdOrderByVisitDateDesc(Long patientId);

    List<Visit> findByDoctorIdOrderByVisitDateDesc(Long doctorId);

    @Query("SELECT v FROM Visit v WHERE v.doctor.id = :doctorId AND v.visitDate BETWEEN :startDate AND :endDate")
    List<Visit> findVisitsByDoctorAndDateRange(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(v) FROM Visit v WHERE v.visitDate BETWEEN :startDate AND :endDate")
    long countVisitsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    long countByDoctorIdAndVisitDateBetween(Long doctorId, LocalDateTime startDate, LocalDateTime endDate);
}