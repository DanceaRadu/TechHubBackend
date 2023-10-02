package com.tech.techhubbackend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
public @Data class Product {

    @Id
    @Column(name = "product_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productID;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_price", nullable = false)
    private double productPrice;

    @Column(name = "product_description", nullable = false, length = 2000)
    private String description;

    @Column(name = "stock", nullable = false)
    private int stock;

    @JsonManagedReference(value = "product")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, mappedBy = "product")
    private List<ProductImage> productImages;

    @JsonManagedReference(value = "reviewedProduct")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, mappedBy = "reviewedProduct")
    private List<Review> productReviews;
}
