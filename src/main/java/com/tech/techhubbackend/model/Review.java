package com.tech.techhubbackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "reviews")
public @Data class Review {

    @Id
    @Column(name = "review_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reviewID;

    @JsonBackReference(value = "reviewer")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer", referencedColumnName = "user_id", nullable = false)
    private User reviewer;

    @JsonBackReference(value = "reviewedProduct")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id", nullable = false)
    private Product reviewedProduct;

    @Column(name = "review_title", nullable = false, length = 200)
    private String reviewTitle;

    @Column(name = "review_comment", nullable = false, length = 2000)
    private String reviewComment;

    @Column(name = "review_score", nullable = false)
    private int reviewScore;

    @Column(name = "post_date")
    private LocalDate postDate;
}
