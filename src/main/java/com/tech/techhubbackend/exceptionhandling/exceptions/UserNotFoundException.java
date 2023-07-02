package com.tech.techhubbackend.exceptionhandling.exceptions;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID id) {
        super("User with id= " + id + " was not found");
    }
}
