package com.tech.techhubbackend.DTO.DTOs;

import com.tech.techhubbackend.auth.Role;
import com.tech.techhubbackend.model.Image;
import com.tech.techhubbackend.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public @Data class UserDetailsDTO {

    public UserDetailsDTO(User u) {
        this._username = u.get_username();
        this.firstName = u.getFirstName();
        this.lastName = u.getLastName();
        this.email = u.getEmail();
        this.role = u.getRole();
        this.profileImage = u.getProfileImage();
    }

    private String _username;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private Image profileImage;
}
