package com.tech.techhubbackend.service;

import com.tech.techhubbackend.DTO.DTOs.FavoriteEntryGetDTO;
import com.tech.techhubbackend.exceptionhandling.exceptions.FavoriteEntryNotFoundException;
import com.tech.techhubbackend.exceptionhandling.exceptions.ForbiddenRequestException;
import com.tech.techhubbackend.exceptionhandling.exceptions.ProductNotFoundException;
import com.tech.techhubbackend.exceptionhandling.exceptions.UserNotFoundException;
import com.tech.techhubbackend.model.FavoriteEntry;
import com.tech.techhubbackend.repository.FavoriteEntryRepository;
import com.tech.techhubbackend.repository.ProductRepository;
import com.tech.techhubbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FavoriteEntryService {

    private final FavoriteEntryRepository favoriteEntryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public FavoriteEntryService(FavoriteEntryRepository favoriteEntryRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.favoriteEntryRepository = favoriteEntryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public FavoriteEntryGetDTO addFavorite(UUID productID, UUID userID) {
        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        if(!productRepository.existsById(productID)) throw new ProductNotFoundException(productID);

        FavoriteEntry favoriteEntry = new FavoriteEntry();
        favoriteEntry.setUser(userRepository.getReferenceById(userID));
        favoriteEntry.setProduct(productRepository.getReferenceById(productID));

        favoriteEntryRepository.save(favoriteEntry);
        return new FavoriteEntryGetDTO(favoriteEntry);
    }

    public FavoriteEntry getFavorite(UUID favoriteEntryID, UUID userID) {
        if(!favoriteEntryRepository.existsById(favoriteEntryID)) throw new FavoriteEntryNotFoundException(favoriteEntryID);

        FavoriteEntry entry = favoriteEntryRepository.getReferenceById(favoriteEntryID);
        if(!entry.getUser().getUserID().equals(userID)) throw new ForbiddenRequestException("Can't access the favorite item of another user");

        return entry;
    }

    public void deleteFavorite(UUID favoriteEntryID, UUID userID) {
        if (!favoriteEntryRepository.existsById(favoriteEntryID)) throw new FavoriteEntryNotFoundException(favoriteEntryID);

        FavoriteEntry entry = favoriteEntryRepository.getReferenceById(favoriteEntryID);
        if (!entry.getUser().getUserID().equals(userID)) throw new ForbiddenRequestException("Can't delete the favorite item of another user");

        favoriteEntryRepository.delete(entry);
    }
}