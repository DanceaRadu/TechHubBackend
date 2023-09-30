package com.tech.techhubbackend.controller;

import com.tech.techhubbackend.model.Image;
import com.tech.techhubbackend.model.Product;
import com.tech.techhubbackend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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
    public UUID addProduct(@RequestBody Product p) {
        return productService.addProduct(p);
    }

    @PostMapping(path = "image")
    private void addImage(
            @RequestParam("productID") UUID productID,
            @RequestPart MultipartFile image
    ) {
        productService.addImage(productID, image);
    }

    @GetMapping(path = "{id}/images")
    public List<Image> getProductImages(@PathVariable UUID id) {
        return productService.getProductImages(id);
    }

    @GetMapping("paginate")
    public Page<Product> findAllProductsPaginated(@RequestParam int pageNumber, @RequestParam int pageSize) {
        return productService.findAllProductsPaginated(pageNumber, pageSize);
    }

    //TODO delete
    @GetMapping(path = "all")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
}
