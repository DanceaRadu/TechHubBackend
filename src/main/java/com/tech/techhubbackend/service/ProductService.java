package com.tech.techhubbackend.service;

import com.tech.techhubbackend.exceptionhandling.exceptions.ProductNotFoundException;
import com.tech.techhubbackend.model.Product;
import com.tech.techhubbackend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProduct(UUID id) {
        if(!productRepository.existsById(id)) throw new ProductNotFoundException(id);
        return productRepository.getReferenceById(id);
    }

    public void addProduct(Product p) {
        productRepository.save(p);
    }
}
