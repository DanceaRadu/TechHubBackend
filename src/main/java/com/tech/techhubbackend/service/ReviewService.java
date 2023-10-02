package com.tech.techhubbackend.service;

import com.tech.techhubbackend.exceptionhandling.exceptions.ReviewNotFoundException;
import com.tech.techhubbackend.model.Review;
import com.tech.techhubbackend.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review getReview(UUID id) {
        if(!reviewRepository.existsById(id)) throw new ReviewNotFoundException(id);
        return reviewRepository.getReferenceById(id);
    }

    public void postReview(Review r) {
        reviewRepository.save(r);
    }

    public void deleteReview(UUID id) {
        if(!reviewRepository.existsById(id)) throw new ReviewNotFoundException(id);
        reviewRepository.deleteById(id);
    }
}