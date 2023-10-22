package com.tech.techhubbackend.DTO.DTOs;

import com.tech.techhubbackend.model.FavoriteEntry;
import com.tech.techhubbackend.model.Product;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
public @Data class FavoriteEntryGetDTO {

    private UUID favoriteID;
    private UUID productID;
    private UUID userID;
    private Product product;

    public FavoriteEntryGetDTO(FavoriteEntry entry) {
        this.favoriteID = entry.getFavoriteID();
        this.productID = entry.getProduct().getProductID();
        this.userID = entry.getUser().getUserID();
        this.product = entry.getProduct();
    }
}
