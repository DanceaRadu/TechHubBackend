package com.tech.techhubbackend.exceptionhandling.exceptions;

public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(String message) { super(message); }
}