package com.tech.techhubbackend.exceptionhandling.exceptions;

public class ImageNotPresentException extends RuntimeException {
    public ImageNotPresentException() {
        super("The image was not included");
    }
}