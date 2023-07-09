package com.tech.techhubbackend.exceptionhandling.exceptions;

import java.util.UUID;

public class ShoppingCartEntryNotFoundException extends RuntimeException {
    public ShoppingCartEntryNotFoundException(UUID entryID) {
        super("Shopping cart entry with id = " + entryID + " was not found");
    }
}
