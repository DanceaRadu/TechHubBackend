package com.tech.techhubbackend.exceptionhandling.exceptions;

public class EmailVerificationNotFoundException extends RuntimeException {
    public EmailVerificationNotFoundException() {
        super("Email verification for this user doesn't exist");
    }
}