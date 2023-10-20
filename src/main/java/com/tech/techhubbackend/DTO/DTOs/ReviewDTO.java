package com.tech.techhubbackend.DTO.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tech.techhubbackend.model.Product;
import com.tech.techhubbackend.model.Review;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
public @Data class ReviewDTO {

    public ReviewDTO(Review r) {
        this.reviewComment = r.getReviewComment();
        this.reviewTitle = r.getReviewTitle();
        this.reviewScore = r.getReviewScore();
        this.reviewID = r.getReviewID();
        this.postDate = r.getPostDate();
        this.reviewedProduct = r.getReviewedProduct();
    }

    private Product reviewedProduct;
    @JsonProperty("reviewID")
    private UUID reviewID;
    private String reviewComment;
    private String reviewTitle;
    private int reviewScore;
    @JsonProperty("postDate")
    private LocalDate postDate;
}