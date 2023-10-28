package com.tech.techhubbackend.DTO.DTOs;

import lombok.Data;

public @Data class ReviewUpdateDTO {
    private String reviewComment;
    private String reviewTitle;
    private int reviewScore;
}
