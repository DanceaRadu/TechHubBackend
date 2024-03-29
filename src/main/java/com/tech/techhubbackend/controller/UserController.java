package com.tech.techhubbackend.controller;

import com.github.fge.jsonpatch.JsonPatch;
import com.tech.techhubbackend.DTO.DTOs.FavoriteEntryGetDTO;
import com.tech.techhubbackend.DTO.DTOs.ReviewDTO;
import com.tech.techhubbackend.DTO.DTOs.ShoppingCartEntryDTO;
import com.tech.techhubbackend.DTO.DTOs.UserDetailsDTO;
import com.tech.techhubbackend.model.ShoppingCartEntry;
import com.tech.techhubbackend.service.JwtService;
import com.tech.techhubbackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping
    private UserDetailsDTO getUserDetails(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return userService.getUserDetails(UUID.fromString(jwtService.extractID(token)));
    }

    @GetMapping(path = "profilepicture",  produces = MediaType.IMAGE_JPEG_VALUE)
    private @ResponseBody ResponseEntity<byte[]> getUserPicture(HttpServletRequest request){
        String token = request.getHeader("Authorization").substring(7);
        System.out.println(UUID.fromString(jwtService.extractID(token)));
        byte[] image;
        try {
            image = userService.getUserPicture(UUID.fromString(jwtService.extractID(token))).getContentAsByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(image, headers, 200);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(path = "profilepicture")
    private void addUserProfilePicture(@RequestPart MultipartFile image, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        userService.addUserProfilePicture(UUID.fromString(jwtService.extractID(token)), image);
    }

    @GetMapping(path="shoppingcart")
    private List<ShoppingCartEntryDTO> getUserShoppingCart(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return userService.getUserShoppingCart(UUID.fromString(jwtService.extractID(token)));
    }

    @PostMapping(path = "shoppingcart/{productID}")
    private ShoppingCartEntryDTO addShoppingCartItem(HttpServletRequest request, @PathVariable UUID productID) {
        String token = request.getHeader("Authorization").substring(7);
        return userService.addShoppingCartItem(UUID.fromString(jwtService.extractID(token)), productID);
    }

    @DeleteMapping(path="shoppingcart/{productID}")
    private void deleteShoppingCartEntry(HttpServletRequest request, @PathVariable UUID productID) {
        String token = request.getHeader("Authorization").substring(7);
        userService.deleteShoppingCartItem(UUID.fromString(jwtService.extractID(token)), productID);
    }

    @PutMapping(path = "shoppingcart")
    private void updateQuantity(@RequestBody ShoppingCartEntry newEntry, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        userService.updateQuantity(UUID.fromString(jwtService.extractID(token)), newEntry);
    }

    @PatchMapping(consumes = "application/json-patch+json")
    public void patchUser(@RequestBody JsonPatch patch, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        userService.patchUser(UUID.fromString(jwtService.extractID(token)), patch);
    }

    @GetMapping(path = "reviews")
    private List<ReviewDTO> getUserReviews(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return userService.getUserReviews(UUID.fromString(jwtService.extractID(token)));
    }

    @GetMapping(path = "favorites")
    private List<FavoriteEntryGetDTO> getUserFavorites(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return userService.getUserFavorites(UUID.fromString(jwtService.extractID(token)));
    }

    @PostMapping(path = "email/update/{email}")
    private void updateEmail(@PathVariable String email, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        userService.updateEmail(UUID.fromString(jwtService.extractID(token)), email);
    }

    @GetMapping(path = "/verified")
    private boolean checkVerifiedEmailStatus(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return userService.checkVerifiedEmailStatus(UUID.fromString(jwtService.extractID(token)));
    }
}