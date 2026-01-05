package com.bharatemr.repository;

import com.bharatemr.enums.FollowUpStatus;
import com.bharatemr.model.FollowUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FollowUpRepository extends JpaRepository<FollowUp, Long> {

    List<FollowUp> findByPatientIdOrderByScheduledDateDesc(Long patientId);

    List<FollowUp> findByDoctorIdOrderByScheduledDateDesc(Long doctorId);

    List<FollowUp> findByStatusOrderByScheduledDateAsc(FollowUpStatus status);

    @Query("SELECT f FROM FollowUp f WHERE f.doctor.id = :doctorId AND f.scheduledDate BETWEEN :startDate AND :endDate")
    List<FollowUp> findFollowUpsByDoctorAndDateRange(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(f) FROM FollowUp f WHERE DATE(f.scheduledDate) = CURRENT_DATE AND f.status = 'SCHEDULED'")
    long countTodaysScheduledFollowUps();

    long countByDoctorIdAndStatus(Long doctorId, FollowUpStatus status);
}