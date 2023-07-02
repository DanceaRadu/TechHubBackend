package com.tech.techhubbackend.repository;

import com.tech.techhubbackend.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
}
