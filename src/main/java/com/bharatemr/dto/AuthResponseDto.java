package com.bharatemr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private String refreshToken;
    private String userId;
    private String name;
    private String userType;
    private String specialization;
    private Long expiresIn;
    private String preferredTheme;
}