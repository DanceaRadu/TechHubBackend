package com.tech.techhubbackend.exceptionhandling.exceptions;

public class ForbiddenRequestException extends RuntimeException {
    public ForbiddenRequestException(String message) {
        super(message);
    }
}