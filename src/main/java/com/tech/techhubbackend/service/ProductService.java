package com.tech.techhubbackend.service;

import com.tech.techhubbackend.exceptionhandling.exceptions.ImageNotPresentException;
import com.tech.techhubbackend.exceptionhandling.exceptions.ProductNotFoundException;
import com.tech.techhubbackend.model.Image;
import com.tech.techhubbackend.model.Product;
import com.tech.techhubbackend.model.ProductImage;
import com.tech.techhubbackend.repository.ImageRepository;
import com.tech.techhubbackend.repository.ProductImageRepository;
import com.tech.techhubbackend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final ProductImageRepository productImageRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, ImageRepository imageRepository, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
        this.productImageRepository = productImageRepository;
    }

    public Product getProduct(UUID id) {
        if(!productRepository.existsById(id)) throw new ProductNotFoundException(id);
        return productRepository.getReferenceById(id);
    }

    public UUID addProduct(Product p) {
        productRepository.save(p);
        try {
            Path path = Paths.get("D:/TechHub/images/product/" + p.getProductID());
            Files.createDirectories(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p.getProductID();
    }

    public void addImage(UUID productID, MultipartFile image) {
        String uploadDirectory = "D:/TechHub/images/product/" + productID;
        if(!productRepository.existsById(productID)) throw new ProductNotFoundException(productID);

        try {
            if (image.isEmpty()) throw new ImageNotPresentException();

            String filename = image.getOriginalFilename();
            // Create a unique file name based on productID and provided filename
            String uniqueFileName = generateUniqueFileName(filename, productID);

            // Construct the file path where the image will be saved
            Path filePath = Path.of(uploadDirectory, uniqueFileName);

            // Save the image file to disk
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            //create a new image entity based on the file that was just saved to disk
            Image imageEntity = new Image();
            imageEntity.setFilename(uniqueFileName);
            imageEntity.setFilePath(uploadDirectory);
            imageRepository.save(imageEntity);

            ProductImage productImage = new ProductImage();
            productImage.setImage(imageEntity);
            productImage.setProduct(productRepository.getReferenceById(productID));
            productImageRepository.save(productImage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Image> getProductImages(UUID productID) {
        if(!productRepository.existsById(productID)) throw new ProductNotFoundException(productID);
        return productImageRepository.getProductImagesByProduct(productRepository.getReferenceById(productID)).stream().map(ProductImage::getImage).toList();
    }

    private String generateUniqueFileName(String filename, UUID productID) {
        String originalFileName = StringUtils.cleanPath(filename);
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return productID.toString() + "_" + UUID.randomUUID() + extension;
    }

    public Page<Product> findAllProductsPaginated(int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        return productRepository.findAll(pageRequest);
    }

    //TODO delete
    public List<Product> getAllProducts() {
        return productRepository.getProductsByDescriptionContains("");
    }
}
