package com.tech.techhubbackend.DTO.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public @Data class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
