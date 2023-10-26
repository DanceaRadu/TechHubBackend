package com.tech.techhubbackend.DTO.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public @Data class AuthenticationRequest {
    private String username;
    private String password;
}
