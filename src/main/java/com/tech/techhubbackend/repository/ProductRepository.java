package com.tech.techhubbackend.repository;

import com.tech.techhubbackend.model.Product;
import com.tech.techhubbackend.model.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> getProductsByDescriptionContains(String contains);

    //method for the admin page in the frontend
    Page<Product> findAllByProductNameContainingIgnoreCase(Pageable p, String productName);
    //
    Page<Product> findAllByProductNameContainingIgnoreCaseOrderByProductNameAsc(Pageable p, String productName);
    Page<Product> findAllByProductCategoryAndProductNameContainingIgnoreCaseOrderByProductPrice(Pageable p, ProductCategory pc, String productName);
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
}