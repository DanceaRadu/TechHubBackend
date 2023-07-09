package com.tech.techhubbackend.service;

import com.tech.techhubbackend.DTO.DTOs.UserDetailsDTO;
import com.tech.techhubbackend.DTO.mappers.DTOMapper;
import com.tech.techhubbackend.exceptionhandling.exceptions.*;
import com.tech.techhubbackend.model.Image;
import com.tech.techhubbackend.model.Product;
import com.tech.techhubbackend.model.ShoppingCartEntry;
import com.tech.techhubbackend.repository.ProductRepository;
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
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ShoppingCartEntryRepository shoppingCartEntryRepository;
    private final DTOMapper dtoMapper;

    @Autowired
    public UserService(UserRepository userRepository, DTOMapper dtoMapper, ProductRepository productRepository, ShoppingCartEntryRepository shoppingCartEntryRepository) {
        this.userRepository = userRepository;
        this.dtoMapper = dtoMapper;
        this.productRepository = productRepository;
        this.shoppingCartEntryRepository = shoppingCartEntryRepository;
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

    public List<Product> getUserShoppingCart(UUID userID) {
        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        return userRepository.getReferenceById(userID).getShoppingCartEntries().stream().map(ShoppingCartEntry::getProduct).toList();
    }

    public void addShoppingCartItem(UUID userID, UUID productID) {
        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        if(!productRepository.existsById(productID)) throw new ProductNotFoundException(productID);

        ShoppingCartEntry shoppingCartEntry = new ShoppingCartEntry();
        shoppingCartEntry.setProduct(productRepository.getReferenceById(productID));
        shoppingCartEntry.setUser(userRepository.getReferenceById(userID));
        shoppingCartEntry.setQuantity(1);
        shoppingCartEntryRepository.save(shoppingCartEntry);
    }

    public void updateQuantity(UUID userID, ShoppingCartEntry newEntry) {
        if(!shoppingCartEntryRepository.existsById(newEntry.getShoppingCartEntryID())) throw new ShoppingCartEntryNotFoundException(newEntry.getShoppingCartEntryID());
        ShoppingCartEntry oldEntry = shoppingCartEntryRepository.getReferenceById(newEntry.getShoppingCartEntryID());
        if(!oldEntry.getUser().getUserID().equals(userID)) throw new ForbiddenRequestException("Cannot update the shopping cart entry of another user");

        shoppingCartEntryRepository.save(newEntry);
    }
}