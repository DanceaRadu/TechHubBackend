package com.tech.techhubbackend.service;

import com.tech.techhubbackend.exceptionhandling.exceptions.CategoryNotFoundException;
import com.tech.techhubbackend.model.ProductCategory;
import com.tech.techhubbackend.repository.ProductCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    @Autowired
    public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    public void addCategory(ProductCategory pr) {
        productCategoryRepository.save(pr);
    }
    public void deleteCategory(UUID id) {
        if(!productCategoryRepository.existsById(id)) throw new CategoryNotFoundException(id);
        productCategoryRepository.deleteById(id);
    }
    public ProductCategory getCategory(UUID id) {
        if(!productCategoryRepository.existsById(id)) throw new CategoryNotFoundException(id);
        return productCategoryRepository.getReferenceById(id);
    }
    public List<ProductCategory> getAllCategories() {
        return productCategoryRepository.findAll();
    }
}