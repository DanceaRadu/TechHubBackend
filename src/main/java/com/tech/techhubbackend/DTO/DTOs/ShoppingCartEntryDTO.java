package com.tech.techhubbackend.DTO.DTOs;

import com.tech.techhubbackend.model.Product;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public @Data class ShoppingCartEntryDTO {

    private String shoppingCartEntryID;
    private Product product;
    private int quantity;
}