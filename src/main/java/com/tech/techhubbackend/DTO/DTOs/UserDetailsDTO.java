package com.tech.techhubbackend.DTO.DTOs;

import com.tech.techhubbackend.auth.Role;
import com.tech.techhubbackend.model.Image;
import lombok.Data;

public @Data class UserDetailsDTO {

    private String _username;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private Image profileImage;
}
