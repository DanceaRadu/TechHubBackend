package com.tech.techhubbackend.repository;

import com.tech.techhubbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> getUserBy_username(String username);
    Optional<User> getUserByGoogleId(String id);
    boolean existsBy_username(String username);
    boolean existsByGoogleId(String id);
    boolean existsByEmail(String email);
}