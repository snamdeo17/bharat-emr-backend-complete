package com.bharatemr.repository;

import com.bharatemr.enums.OtpPurpose;
import com.bharatemr.model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpVerification, Long> {
    
    Optional<OtpVerification> findTopByMobileNumberAndPurposeOrderByCreatedAtDesc(
        String mobileNumber, 
        OtpPurpose purpose
    );
    
    @Query("SELECT o FROM OtpVerification o WHERE o.mobileNumber = :mobile AND o.otp = :otp " +
           "AND o.purpose = :purpose AND o.isVerified = false AND o.expiryTime > :currentTime")
    Optional<OtpVerification> findValidOtp(
        @Param("mobile") String mobileNumber,
        @Param("otp") String otp,
        @Param("purpose") OtpPurpose purpose,
        @Param("currentTime") LocalDateTime currentTime
    );
    
    @Modifying
    @Transactional
    @Query("DELETE FROM OtpVerification o WHERE o.expiryTime < :currentTime")
    void deleteExpiredOtps(@Param("currentTime") LocalDateTime currentTime);
}