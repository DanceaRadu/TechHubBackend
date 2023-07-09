package com.tech.techhubbackend.repository;

import com.tech.techhubbackend.model.Product;
import com.tech.techhubbackend.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
    List<ProductImage> getProductImagesByProduct(Product product);
}
