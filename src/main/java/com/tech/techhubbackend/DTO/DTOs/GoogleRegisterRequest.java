package com.tech.techhubbackend.DTO.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public @Data class GoogleRegisterRequest {

    //snake case used because this is how google sends the fields in the user info response
    private String email;
    private String family_name;
    private String given_name;
    private String id;
    private String locale;
    private String name;
    private String picture;
    private boolean verified_email;
}
