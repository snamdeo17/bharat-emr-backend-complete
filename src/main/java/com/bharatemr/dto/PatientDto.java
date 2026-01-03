package com.bharatemr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDto {
    
    private Long id;
    private String patientId;
    
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 255)
    private String fullName;
    
    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other")
    private String gender;
    
    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 150, message = "Age seems invalid")
    private Integer age;
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid mobile number format")
    private String mobileNumber;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String address;
    private Long onboardedByDoctorId;
    private String onboardedByDoctorName;
    private String onboardedByDoctorId_str;
    private Boolean isActive;
    private LocalDateTime createdAt;
}