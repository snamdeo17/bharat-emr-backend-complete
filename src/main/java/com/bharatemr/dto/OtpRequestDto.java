package com.bharatemr.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class OtpRequestDto {
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid mobile number format")
    private String mobileNumber;
    
    @NotBlank(message = "Purpose is required")
    @Pattern(regexp = "^(REGISTRATION|LOGIN|PASSWORD_RESET)$")
    private String purpose;
}