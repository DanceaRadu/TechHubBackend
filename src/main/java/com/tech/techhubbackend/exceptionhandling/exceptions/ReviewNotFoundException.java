package com.tech.techhubbackend.exceptionhandling.exceptions;

import java.util.UUID;

public class ReviewNotFoundException extends RuntimeException {

    public ReviewNotFoundException(UUID id) {
        super("Review with id: " + id + " was not found");
    }
}