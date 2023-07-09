package com.tech.techhubbackend.controller;

import com.tech.techhubbackend.model.Product;
import com.tech.techhubbackend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/product")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(path = "{id}")
    private Product getProduct(@PathVariable UUID id) {
        return productService.getProduct(id);
    }

    @PostMapping
    private void addProduct(@RequestBody Product p) {
        productService.addProduct(p);
    }
}
