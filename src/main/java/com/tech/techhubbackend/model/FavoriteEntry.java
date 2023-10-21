package com.tech.techhubbackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Table(name = "favorites")
@Entity
public @Data class FavoriteEntry {

    @Id
    @Column(name = "favorite_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID favoriteID;

    @JsonBackReference(value = "favoriteProduct")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id", nullable = false)
    private Product product;

    @JsonBackReference(value = "favoriteUser")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id" , nullable = false)
    private User user;
}