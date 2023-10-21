package com.tech.techhubbackend.exceptionhandling.exceptions;

import java.util.UUID;

public class FavoriteEntryNotFoundException extends RuntimeException {
    public FavoriteEntryNotFoundException(UUID favoriteEntryID) {
        super("Could not find favorite entry with id: " + favoriteEntryID);
    }
}
