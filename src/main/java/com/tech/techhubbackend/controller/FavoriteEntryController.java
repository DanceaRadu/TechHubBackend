package com.tech.techhubbackend.controller;

import com.tech.techhubbackend.DTO.DTOs.FavoriteEntryGetDTO;
import com.tech.techhubbackend.model.FavoriteEntry;
import com.tech.techhubbackend.service.FavoriteEntryService;
import com.tech.techhubbackend.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/favorite")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FavoriteEntryController {

    private final FavoriteEntryService favoriteEntryService;
    private final JwtService jwtService;

    @Autowired
    public FavoriteEntryController(FavoriteEntryService favoriteEntryService, JwtService jwtService) {
        this.favoriteEntryService = favoriteEntryService;
        this.jwtService = jwtService;
    }

    @PostMapping(path = "/{productID}")
    public FavoriteEntryGetDTO postFavoriteEntry(@PathVariable UUID productID, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return favoriteEntryService.addFavorite(productID, UUID.fromString(jwtService.extractID(token)));
    }

    @GetMapping(path = "/{favoriteID}")
    public FavoriteEntry getFavoriteEntry(@PathVariable UUID favoriteID, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return favoriteEntryService.getFavorite(favoriteID, UUID.fromString(jwtService.extractID(token)));
    }

    @DeleteMapping(path = "/{favoriteID}")
    public void deleteFavoriteEntry(@PathVariable UUID favoriteID, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        favoriteEntryService.deleteFavorite(favoriteID, UUID.fromString(jwtService.extractID(token)));
    }
}
