package com.tech.techhubbackend.exceptionhandling.exceptions;

import java.util.UUID;

public class ImageNotFoundException extends RuntimeException{
    public ImageNotFoundException(UUID id) {
        super("Image with id= " + id + " was not found");
    }
}
