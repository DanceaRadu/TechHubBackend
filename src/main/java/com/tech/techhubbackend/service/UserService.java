package com.tech.techhubbackend.service;

import com.tech.techhubbackend.DTO.DTOs.ReviewDTO;
import com.tech.techhubbackend.DTO.DTOs.ShoppingCartEntryDTO;
import com.tech.techhubbackend.DTO.DTOs.UserDetailsDTO;
import com.tech.techhubbackend.DTO.mappers.DTOMapper;
import com.tech.techhubbackend.exceptionhandling.exceptions.*;
import com.tech.techhubbackend.model.Image;
import com.tech.techhubbackend.model.ShoppingCartEntry;
import com.tech.techhubbackend.model.User;
import com.tech.techhubbackend.repository.ProductRepository;
import com.tech.techhubbackend.repository.ReviewRepository;
import com.tech.techhubbackend.repository.ShoppingCartEntryRepository;
import com.tech.techhubbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ShoppingCartEntryRepository shoppingCartEntryRepository;
    private final ReviewRepository reviewRepository;
    private final DTOMapper dtoMapper;

    @Autowired
    public UserService(UserRepository userRepository, DTOMapper dtoMapper, ProductRepository productRepository, ShoppingCartEntryRepository shoppingCartEntryRepository, ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.dtoMapper = dtoMapper;
        this.productRepository = productRepository;
        this.shoppingCartEntryRepository = shoppingCartEntryRepository;
        this.reviewRepository = reviewRepository;
    }

    public UserDetailsDTO getUserDetails(UUID userID) {
        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        return dtoMapper.userToUserDetailsDTO(userRepository.getReferenceById(userID));
    }

    public Resource getUserPicture(UUID userID) {
        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        Image image = userRepository.getReferenceById(userID).getProfileImage();
        if(image == null) throw new ImageNotFoundException();

        Path path;
        path = Paths.get(image.getFilePath() + '/' + image.getFilename());
        try {
            return new ByteArrayResource(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new ImageNotFoundException();
        }
    }

    public List<ShoppingCartEntryDTO> getUserShoppingCart(UUID userID) {
        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        return userRepository.getReferenceById(userID).getShoppingCartEntries().stream().map(dtoMapper::shoppingCartEntryToShoppingCartEntryDTO).toList();
    }

    public ShoppingCartEntryDTO addShoppingCartItem(UUID userID, UUID productID) {
        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        if(!productRepository.existsById(productID)) throw new ProductNotFoundException(productID);

        //check if the product already exists in the shopping cart
        Optional<ShoppingCartEntry> optionalEntry = shoppingCartEntryRepository.getShoppingCartEntryByProductAndUser(productRepository.getReferenceById(productID), userRepository.getReferenceById(userID));
        if(optionalEntry.isPresent()) {
            ShoppingCartEntry entry = optionalEntry.get();
            entry.setQuantity(entry.getQuantity() + 1);
            shoppingCartEntryRepository.save(entry);
            return dtoMapper.shoppingCartEntryToShoppingCartEntryDTO(entry);
        }
        ShoppingCartEntry shoppingCartEntry = new ShoppingCartEntry();
        shoppingCartEntry.setProduct(productRepository.getReferenceById(productID));
        shoppingCartEntry.setUser(userRepository.getReferenceById(userID));
        shoppingCartEntry.setQuantity(1);
        shoppingCartEntry.setShoppingCartEntryID(null);
        shoppingCartEntryRepository.save(shoppingCartEntry);
        return dtoMapper.shoppingCartEntryToShoppingCartEntryDTO(shoppingCartEntry);
    }

    public void deleteShoppingCartItem(UUID userID, UUID productID) {
        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        if(!productRepository.existsById(productID)) throw new ProductNotFoundException(productID);

        Optional<ShoppingCartEntry> shoppingCartEntry = shoppingCartEntryRepository.getShoppingCartEntryByProductAndUser(
                productRepository.getReferenceById(productID),
                userRepository.getReferenceById(userID)
                );

        if(shoppingCartEntry.isPresent()) shoppingCartEntryRepository.delete(shoppingCartEntry.get());
        else throw new ShoppingCartEntryNotFoundException();
    }

    public void updateQuantity(UUID userID, ShoppingCartEntry newEntry) {
        if(!shoppingCartEntryRepository.existsById(newEntry.getShoppingCartEntryID())) throw new ShoppingCartEntryNotFoundException();
        ShoppingCartEntry oldEntry = shoppingCartEntryRepository.getReferenceById(newEntry.getShoppingCartEntryID());
        if(!oldEntry.getUser().getUserID().equals(userID)) throw new ForbiddenRequestException("Cannot update the shopping cart entry of another user");

        shoppingCartEntryRepository.save(newEntry);
    }

    public void updateEmail(UUID userID, String newEmail) {
        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        if(userRepository.existsByEmail(newEmail)) throw new EntityAlreadyExistsException("User with this email already exists");

        User user = userRepository.getReferenceById(userID);
        user.setEmail(newEmail);
        userRepository.save(user);
    }

    public boolean checkVerifiedEmailStatus(UUID userID) {
        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        return userRepository.getReferenceById(userID).isVerified();
    }

    public List<ReviewDTO> getUserReviews(UUID userID) {
        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        User user = userRepository.getReferenceById(userID);
        return reviewRepository.getReviewsByReviewer(user).stream().map(dtoMapper::reviewToReviewDTO).toList();
    }
}