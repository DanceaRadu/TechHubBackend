package com.tech.techhubbackend.repository;

import com.tech.techhubbackend.model.EmailVerification;
import com.tech.techhubbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, UUID> {
    Optional<EmailVerification> getEmailVerificationByUser(User user);
}