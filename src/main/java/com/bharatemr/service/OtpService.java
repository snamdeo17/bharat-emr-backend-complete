package com.bharatemr.service;

import com.bharatemr.enums.OtpPurpose;
import com.bharatemr.exception.InvalidOtpException;
import com.bharatemr.model.OtpVerification;
import com.bharatemr.repository.OtpRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Slf4j
public class OtpService {
    
    @Autowired
    private OtpRepository otpRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Value("${app.otp.expiration}")
    private Long otpExpiration;
    
    @Value("${app.otp.length}")
    private Integer otpLength;
    
    @Transactional
    public String generateAndSendOtp(String mobileNumber, OtpPurpose purpose) {
        String otp = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(otpExpiration / 1000);
        
        OtpVerification otpVerification = OtpVerification.builder()
                .mobileNumber(mobileNumber)
                .otp(otp)
                .purpose(purpose)
                .expiryTime(expiryTime)
                .isVerified(false)
                .build();
        
        otpRepository.save(otpVerification);
        
        // Send OTP via SMS
        String message = String.format(
            "Your Bharat EMR OTP for %s is: %s. Valid for %d minutes. Do not share with anyone.",
            purpose.name().toLowerCase().replace("_", " "),
            otp,
            (otpExpiration / 1000) / 60
        );
        
        notificationService.sendSms(mobileNumber, message);
        
        log.info("OTP generated and sent to: {} for purpose: {}", mobileNumber, purpose);
        return otp; // For testing purposes, in production don't return this
    }
    
    @Transactional
    public boolean verifyOtp(String mobileNumber, String otp, OtpPurpose purpose) {
        OtpVerification otpVerification = otpRepository.findValidOtp(
                mobileNumber, 
                otp, 
                purpose, 
                LocalDateTime.now()
        ).orElseThrow(() -> new InvalidOtpException("Invalid or expired OTP"));
        
        if (otpVerification.getIsVerified()) {
            throw new InvalidOtpException("OTP already used");
        }
        
        otpVerification.setIsVerified(true);
        otpRepository.save(otpVerification);
        
        log.info("OTP verified successfully for: {}", mobileNumber);
        return true;
    }
    
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }
    
    @Scheduled(fixedRate = 3600000) // Run every hour
    @Transactional
    public void cleanupExpiredOtps() {
        otpRepository.deleteExpiredOtps(LocalDateTime.now());
        log.info("Expired OTPs cleaned up");
    }
}