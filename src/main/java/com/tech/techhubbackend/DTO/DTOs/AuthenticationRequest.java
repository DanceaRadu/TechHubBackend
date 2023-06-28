package com.tech.techhubbackend.DTO.DTOs;

import lombok.Data;

public @Data class AuthenticationRequest {
    private String username;
    private String password;
}
