package com.tech.techhubbackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "product_images")
public @Data class ProductImage {

    @Id
    @Column(name = "product_image_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productImageID;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id", referencedColumnName = "image_id")
    private Image image;

    @JsonBackReference(value = "product")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

}
