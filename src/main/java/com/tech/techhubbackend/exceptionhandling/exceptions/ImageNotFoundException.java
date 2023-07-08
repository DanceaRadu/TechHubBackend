package com.tech.techhubbackend.exceptionhandling.exceptions;

public class ImageNotFoundException extends RuntimeException{
    public ImageNotFoundException() {
        super("Image was not found");
    }
}
