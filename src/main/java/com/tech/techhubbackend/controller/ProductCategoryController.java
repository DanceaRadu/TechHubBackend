package com.tech.techhubbackend.controller;

import com.tech.techhubbackend.model.ProductCategory;
import com.tech.techhubbackend.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/category")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @Autowired
    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @GetMapping("/{id}")
    public ProductCategory getCategory(@PathVariable UUID id) {
        return productCategoryService.getCategory(id);
    }

    @PostMapping
    public void addCategory(@RequestBody ProductCategory pr) {
        productCategoryService.addCategory(pr);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable UUID id) {
        productCategoryService.deleteCategory(id);
    }

    @GetMapping("/all")
    public List<ProductCategory> getAllCategories() {
        return productCategoryService.getAllCategories();
    }
}