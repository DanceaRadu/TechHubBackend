package com.tech.techhubbackend.DTO.DTOs;

import com.tech.techhubbackend.model.Product;
import lombok.Data;

public @Data class ShoppingCartEntryDTO {

    private String shoppingCartEntryID;
    private Product product;
    private int quantity;
}