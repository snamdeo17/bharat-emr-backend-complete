package com.bharatemr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestDto {
    
    private Long id;
    
    @NotBlank(message = "Test name is required")
    private String testName;
    
    private String testType;
    private String instructions;
}