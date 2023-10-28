package com.tech.techhubbackend.service;

import com.tech.techhubbackend.DTO.DTOs.ReviewDTO;
import com.tech.techhubbackend.DTO.DTOs.ReviewUpdateDTO;
import com.tech.techhubbackend.DTO.mappers.DTOMapper;
import com.tech.techhubbackend.exceptionhandling.exceptions.ForbiddenRequestException;
import com.tech.techhubbackend.exceptionhandling.exceptions.ProductNotFoundException;
import com.tech.techhubbackend.exceptionhandling.exceptions.ReviewNotFoundException;
import com.tech.techhubbackend.exceptionhandling.exceptions.UserNotFoundException;
import com.tech.techhubbackend.model.Review;
import com.tech.techhubbackend.model.User;
import com.tech.techhubbackend.repository.ProductRepository;
import com.tech.techhubbackend.repository.ReviewRepository;
import com.tech.techhubbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        review.setPostDate(LocalDate.now());
        reviewRepository.save(review);
    }

    public void deleteReview(UUID reviewID, UUID userID) {
        if(!reviewRepository.existsById(reviewID)) throw new ReviewNotFoundException(reviewID);
        if(!userRepository.existsById(userID)) throw new UserNotFoundException(userID);
        if(!reviewRepository.getReferenceById(reviewID).getReviewer().getUserID().equals(userID)) throw new ForbiddenRequestException("Cannot delete a review of another user");
        reviewRepository.deleteById(reviewID);
    }

    public void putReview(ReviewUpdateDTO newReviewContent, UUID userId, UUID reviewID) {
        if(!reviewRepository.existsById(reviewID)) throw new ReviewNotFoundException(reviewID);
        if(!userRepository.existsById(userId)) throw new UserNotFoundException(userId);

        User user = userRepository.getReferenceById(userId);
        Review review = reviewRepository.getReferenceById(reviewID);
        if(!review.getReviewer().getUserID().equals(user.getUserID())) throw new ForbiddenRequestException("Can't edit another user's review");

        review.setReviewComment(newReviewContent.getReviewComment());
        review.setReviewTitle(newReviewContent.getReviewTitle() + " (edited)");
        review.setReviewScore(newReviewContent.getReviewScore());
        reviewRepository.save(review);
    }
}