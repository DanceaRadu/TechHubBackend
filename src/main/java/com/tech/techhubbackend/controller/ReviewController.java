package com.tech.techhubbackend.controller;

import com.tech.techhubbackend.model.Review;
import com.tech.techhubbackend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/review")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable UUID id) {
        return reviewService.getReview(id);
    }

    @PostMapping
    public void postReview(@RequestBody Review r) {
        reviewService.postReview(r);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable UUID id) {
        reviewService.deleteReview(id);
    }
}