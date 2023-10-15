package com.tech.techhubbackend.repository;

import com.tech.techhubbackend.model.Review;
import com.tech.techhubbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
        List<Review> getReviewsByReviewer(User reviewer);
}