package com.bharatemr.controller;

import com.bharatemr.dto.ApiResponse;
import com.bharatemr.dto.OtpRequestDto;
import com.bharatemr.dto.OtpVerificationDto;
import com.bharatemr.enums.OtpPurpose;
import com.bharatemr.service.OtpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/otp")
@CrossOrigin(origins = "*")
@Slf4j
public class OtpController {
    
    @Autowired
    private OtpService otpService;
    
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendOtp(@Valid @RequestBody OtpRequestDto request) {
        log.info("OTP request received for: {}", request.getMobileNumber());
        
        OtpPurpose purpose = OtpPurpose.valueOf(request.getPurpose());
        String otp = otpService.generateAndSendOtp(request.getMobileNumber(), purpose);
        
        // In production, don't return OTP in response
        String message = "OTP sent successfully to " + request.getMobileNumber();
        
        // For development/testing, include OTP in response
        if (System.getenv("SPRING_PROFILES_ACTIVE") != null && 
            System.getenv("SPRING_PROFILES_ACTIVE").equals("dev")) {
            message += " (Dev Mode - OTP: " + otp + ")";
        }
        
        return ResponseEntity.ok(ApiResponse.success(message, otp));
    }
    
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyOtp(@Valid @RequestBody OtpVerificationDto request) {
        log.info("OTP verification request for: {}", request.getMobileNumber());
        
        OtpPurpose purpose = OtpPurpose.valueOf(request.getPurpose());
        boolean isValid = otpService.verifyOtp(
            request.getMobileNumber(), 
            request.getOtp(), 
            purpose
        );
        
        if (isValid) {
            return ResponseEntity.ok(ApiResponse.success("OTP verified successfully", true));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid or expired OTP"));
        }
    }
}