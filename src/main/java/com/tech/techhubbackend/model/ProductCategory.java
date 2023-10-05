package com.tech.techhubbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "categories")
public @Data class ProductCategory {
    @Id
    @Column(name = "category_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID categoryID;

    @Column(name = "category_name", nullable = false)
    private String categoryName;
}