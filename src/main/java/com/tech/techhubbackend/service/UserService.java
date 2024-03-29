package com.tech.techhubbackend.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.tech.techhubbackend.DTO.DTOs.FavoriteEntryGetDTO;
import com.tech.techhubbackend.DTO.DTOs.ReviewDTO;
import com.tech.techhubbackend.DTO.DTOs.ShoppingCartEntryDTO;
import com.tech.techhubbackend.DTO.DTOs.UserDetailsDTO;
import com.tech.techhubbackend.DTO.mappers.DTOMapper;
import com.tech.techhubbackend.exceptionhandling.exceptions.*;
import com.tech.techhubbackend.model.Image;
import com.tech.techhubbackend.model.ShoppingCartEntry;
import com.tech.techhubbackend.model.User;
import com.tech.techhubbackend.repository.*;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ShoppingCartEntryRepository shoppingCartEntryRepository;
    private final ReviewRepository reviewRepository;
    private final ImageRepository imageRepository;
    private final FavoriteEntryRepository favoriteEntryRepository;
    private final DTOMapper dtoMapper;

    @Autowired
    public UserService(UserRepository userRepository,
                       DTOMapper dtoMapper,
                       ProductRepository productRepository,
                       ShoppingCartEntryRepository shoppingCartEntryRepository,
                       ReviewRepository reviewRepository,
                       FavoriteEntryRepository favoriteEntryRepository,
                       ImageRepository imageRepository
                       ) {
        this.userRepository = userRepository;
        this.dtoMapper = dtoMapper;
        this.productRepository = productRepository;
        this.shoppingCartEntryRepository = shoppingCartEntryRepository;
        this.reviewRepository = reviewRepository;
        this.favoriteEntryRepository = favoriteEntryRepository;
        this.imageRepository = imageRepository;
    }

    public UserDetailsDTO getUserDetails(UUID userID) {
        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        return new UserDetailsDTO(userRepository.getReferenceById(userID));
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

    public void addUserProfilePicture(UUID userID, MultipartFile image) {
        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        String uploadDirectory = "D:/TechHub/images/user/" + userID;

        try {
            Resource resource = new FileSystemResource(uploadDirectory);
            if (!resource.exists() || !resource.getFile().isDirectory()) {
                if (!resource.getFile().mkdirs())
                    throw new InternalServerErrorException("Could not create user folder");
            }

            FileUtils.cleanDirectory(resource.getFile());
            User user = userRepository.getReferenceById(userID);
            if(user.getProfileImage() != null) {
                Image tempImage = user.getProfileImage();
                user.setProfileImage(null);
                userRepository.save(user);
                imageRepository.delete(tempImage);
            }
            if (image.isEmpty()) throw new ImageNotPresentException();

            String filename = image.getOriginalFilename();
            // Create a unique file name based on productID and provided filename
            String uniqueFileName = generateUniqueFileName(filename, userID);

            // Construct the file path where the image will be saved
            Path filePath = Path.of(uploadDirectory, uniqueFileName);

            // Save the image file to disk
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            //create a new image entity based on the file that was just saved to disk
            Image imageEntity = new Image();
            imageEntity.setFilename(uniqueFileName);
            imageEntity.setFilePath(uploadDirectory);
            imageRepository.save(imageEntity);

            user = userRepository.getReferenceById(userID);
            user.setProfileImage(imageEntity);
            userRepository.save(user);

        } catch (IOException e) {
            throw new InternalServerErrorException("Could not create user folder");
        }
    }

    public static String generateUniqueFileName(String filename, UUID productID) {
        String originalFileName = StringUtils.cleanPath(filename);
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return productID.toString() + "_" + UUID.randomUUID() + extension;
    }

    public void patchUser(UUID userID, JsonPatch patch) {
        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        try {
            User userPatched;
            Optional<User> optionalUser = userRepository.findById(userID);
            if(optionalUser.isPresent()) {
                User u = optionalUser.get();
                userPatched = applyPatchToUser(patch, u);
            }
            else throw new UserNotFoundException(userID);
            userRepository.save(userPatched);
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new InternalServerErrorException("Error parsing patch request." + e.getMessage());
        }
    }

    private User applyPatchToUser(JsonPatch patch, User user) throws JsonPatchException, JsonProcessingException{
        ObjectMapper o = new ObjectMapper();
        o.findAndRegisterModules();
        o.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        o.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        JsonNode patched = patch.apply(o.convertValue(user, JsonNode.class));
        return o.treeToValue(patched, User.class);
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
        return reviewRepository.getReviewsByReviewer(user).stream().map(ReviewDTO::new).toList();
    }

    public List<FavoriteEntryGetDTO> getUserFavorites(UUID userID) {
        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        User user = userRepository.getReferenceById(userID);
        return favoriteEntryRepository.getFavoriteEntriesByUser(user).stream().map(FavoriteEntryGetDTO::new).collect(Collectors.toList());
    }
}