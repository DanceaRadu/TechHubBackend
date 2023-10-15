package com.tech.techhubbackend.service;

import com.tech.techhubbackend.DTO.DTOs.ReviewDTO;
import com.tech.techhubbackend.DTO.mappers.DTOMapper;
import com.tech.techhubbackend.exceptionhandling.exceptions.ProductNotFoundException;
import com.tech.techhubbackend.exceptionhandling.exceptions.ReviewNotFoundException;
import com.tech.techhubbackend.exceptionhandling.exceptions.UserNotFoundException;
import com.tech.techhubbackend.model.Review;
import com.tech.techhubbackend.repository.ProductRepository;
import com.tech.techhubbackend.repository.ReviewRepository;
import com.tech.techhubbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final DTOMapper dtoMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, DTOMapper dtoMapper, UserRepository userRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.dtoMapper = dtoMapper;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public Review getReview(UUID id) {
        if(!reviewRepository.existsById(id)) throw new ReviewNotFoundException(id);
        return reviewRepository.getReferenceById(id);
    }

    public void postReview(ReviewDTO r, UUID userID) {

        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        if(!productRepository.existsById(r.getReviewedProduct().getProductID())) throw new ProductNotFoundException(r.getReviewedProduct().getProductID());

        Review review = dtoMapper.reviewDTOToReview(r);
        review.setReviewTitle(r.getReviewTitle());
        review.setReviewer(userRepository.getReferenceById(userID));
        reviewRepository.save(review);
    }

    public void deleteReview(UUID id) {
        if(!reviewRepository.existsById(id)) throw new ReviewNotFoundException(id);
        reviewRepository.deleteById(id);
    }
}