package com.tech.techhubbackend.exceptionhandling.exceptions;

import java.util.UUID;

public class CategoryNotFoundException extends RuntimeException{
    public CategoryNotFoundException(UUID id) {
        super("Category with id: " + id + " was not found");
    }
}
