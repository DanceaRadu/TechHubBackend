package com.tech.techhubbackend.repository;

import com.tech.techhubbackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> getProductsByDescriptionContains(String contains);
}