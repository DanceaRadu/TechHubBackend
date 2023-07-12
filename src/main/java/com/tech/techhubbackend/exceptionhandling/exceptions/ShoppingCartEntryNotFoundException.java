package com.tech.techhubbackend.exceptionhandling.exceptions;

public class ShoppingCartEntryNotFoundException extends RuntimeException {
    public ShoppingCartEntryNotFoundException() {
        super("Shopping cart entry was not found");
    }
}
