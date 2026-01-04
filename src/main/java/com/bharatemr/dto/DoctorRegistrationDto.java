package com.bharatemr.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class DoctorRegistrationDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid mobile number format")
    private String mobileNumber;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "Qualification is required")
    private String qualification;

    @NotNull(message = "Years of experience is required")
    @Min(value = 0, message = "Years of experience cannot be negative")
    private Integer experience;

    @NotBlank(message = "Clinic name is required")
    private String clinicName;

    @NotBlank(message = "Clinic address is required")
    private String clinicAddress;

    @NotBlank(message = "Medical registration number is required")
    private String registrationNumber;

    private String city;
    private String state;
    private String pincode;

    private String profilePhotoUrl;

    private String otp;
}