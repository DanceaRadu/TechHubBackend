package com.tech.techhubbackend.DTO.DTOs;

import com.tech.techhubbackend.model.Product;
import lombok.Data;

public @Data class ReviewDTO {

    private Product reviewedProduct;
    private String reviewComment;
    private String reviewTitle;
    private int reviewScore;
}