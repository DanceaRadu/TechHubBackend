package com.tech.techhubbackend.DTO.DTOs;

import lombok.Builder;
import lombok.Data;

@Builder
public @Data class AuthenticationResponse {
    private String token;
}
