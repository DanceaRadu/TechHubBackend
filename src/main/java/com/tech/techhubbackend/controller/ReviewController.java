package com.tech.techhubbackend.controller;

import com.tech.techhubbackend.DTO.DTOs.ReviewDTO;
import com.tech.techhubbackend.model.Review;
import com.tech.techhubbackend.service.JwtService;
import com.tech.techhubbackend.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/review")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtService jwtService;

    @Autowired
    public ReviewController(ReviewService reviewService, JwtService jwtService) {
        this.reviewService = reviewService;
        this.jwtService = jwtService;
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable UUID id) {
        return reviewService.getReview(id);
    }

    @PostMapping
    public void postReview(@RequestBody ReviewDTO r, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        reviewService.postReview(r, UUID.fromString(jwtService.extractID(token)));
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable UUID id, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        reviewService.deleteReview(id, UUID.fromString(jwtService.extractID(token)));
    }
}