package com.tech.techhubbackend.DTO.DTOs;

import com.tech.techhubbackend.model.Review;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
public @Data class ReviewDTOWithUser {

    public ReviewDTOWithUser(Review r) {
        this.reviewComment = r.getReviewComment();
        this.reviewTitle = r.getReviewTitle();
        this.reviewScore = r.getReviewScore();
        this.reviewer = new UserDetailsDTO(r.getReviewer());
        this.reviewID = r.getReviewID();
        this.postDate = r.getPostDate();
    }

    private String reviewComment;
    private String reviewTitle;
    private int reviewScore;
    private UserDetailsDTO reviewer;
    private UUID reviewID;
    private LocalDate postDate;
}
