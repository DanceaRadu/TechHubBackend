package com.tech.techhubbackend.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.tech.techhubbackend.DTO.DTOs.CustomPageDTO;
import com.tech.techhubbackend.DTO.DTOs.ProductSorter;
import com.tech.techhubbackend.exceptionhandling.exceptions.ImageNotPresentException;
import com.tech.techhubbackend.exceptionhandling.exceptions.InternalServerErrorException;
import com.tech.techhubbackend.exceptionhandling.exceptions.ProductNotFoundException;
import com.tech.techhubbackend.model.*;
import com.tech.techhubbackend.repository.ImageRepository;
import com.tech.techhubbackend.repository.ProductCategoryRepository;
import com.tech.techhubbackend.repository.ProductImageRepository;
import com.tech.techhubbackend.repository.ProductRepository;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @PersistenceContext
    private final EntityManager em;

    @Autowired
    public ProductService(ProductRepository productRepository, ImageRepository imageRepository, ProductImageRepository productImageRepository, ProductCategoryRepository productCategoryRepository, EntityManager em) {
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
        this.productImageRepository = productImageRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.em = em;
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
            throw new InternalServerErrorException("Could not add product");
        }
        return p.getProductID();
    }

    public void deleteProduct(UUID uuid) {
        if(!productRepository.existsById(uuid)) throw new ProductNotFoundException(uuid);
        String directoryPath = "D:/TechHub/images/product/" + productRepository.getReferenceById(uuid).getProductID();
        FileSystemUtils.deleteRecursively(new File(directoryPath));
        productRepository.delete(productRepository.getReferenceById(uuid));
    }

    public void patchProduct(UUID uuid, JsonPatch patch) {
        if(!productRepository.existsById(uuid)) throw new ProductNotFoundException(uuid);
        try {
            Product productPatched;
            Optional<Product> optionalProduct = productRepository.findById(uuid);
            if(optionalProduct.isPresent()) {
                Product p = optionalProduct.get();
                productPatched = applyPatchToProduct(patch, p);
            }
            else throw new ProductNotFoundException(uuid);
            productRepository.save(productPatched);
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new InternalServerErrorException("Error parsing patch request." + e.getMessage());
        }
    }

    private Product applyPatchToProduct(JsonPatch patch, Product p) throws JsonPatchException, JsonProcessingException{
        ObjectMapper o = new ObjectMapper();
        o.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        o.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        JsonNode patched = patch.apply(o.convertValue(p, JsonNode.class));
        return o.treeToValue(patched, Product.class);
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
            throw new InternalServerErrorException("Could not save product image");
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

    public Page<Product> findAllProductsByName(int pageNumber, int pageSize, String query) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        return productRepository.findAllByProductNameContainingIgnoreCase(pageRequest, query);
    }

    private Optional<Predicate> filterProducts(String filter, CriteriaBuilder cb, Root<Product> productRoot, CriteriaQuery<Product> cq) {
        if(filter.equals("none")) return Optional.empty();

//        try {
//            Predicate pricePredicate, ratingPredicate = null;
//            JSONObject jsonObject = new JSONObject(filter);
//            int priceLow = jsonObject.getInt("priceLow");
//            int priceHigh = jsonObject.getInt("priceHigh");
//            pricePredicate = cb.between(productRoot.get("productPrice"), priceLow, priceHigh);
//
//            //filter using the min rating
//            int minRating = jsonObject.getInt("minRating");
//            if(minRating != 0) {
//                Join<Product, Review> reviews = productRoot.join("productReviews", JoinType.LEFT);
//                Expression<Double> averageRating = cb.avg(reviews.get("reviewScore"));
//                cq.groupBy(productRoot.get("productID")); // Group by the product_id
//                ratingPredicate = cb.greaterThanOrEqualTo(averageRating, (double) minRating);
//            }
//            if(ratingPredicate != null) return Optional.of(cb.and(ratingPredicate, pricePredicate));
//            else return Optional.of(pricePredicate);
        try {
            JSONObject jsonObject = new JSONObject(filter);
            int priceLow = jsonObject.getInt("priceLow");
            int priceHigh = jsonObject.getInt("priceHigh");
            // Main query: Filter by price range
            Predicate pricePredicate = cb.between(productRoot.get("productPrice"), priceLow, priceHigh);

            // Create a sub-query for filtering by minimum rating
            int minRating = jsonObject.getInt("minRating");
            Subquery<UUID> subquery = null;
            if(minRating != 0) {
                subquery = cq.subquery(UUID.class);
                Root<Product> subqueryProductRoot = subquery.from(Product.class);
                Join<Product, Review> subqueryReviews = subqueryProductRoot.join("productReviews", JoinType.LEFT);
                Expression<Double> subqueryAverageRating = cb.avg(subqueryReviews.get("reviewScore"));
                subquery.select(subqueryProductRoot.get("productID"))
                        .groupBy(subqueryProductRoot.get("productID"))
                        .having(cb.greaterThanOrEqualTo(subqueryAverageRating, (double) minRating));
                // Apply the sub-query in the WHERE clause
                cq.where(cb.in(productRoot.get("productID")).value(subquery));
            }
            if(subquery != null) return Optional.of(cb.and(pricePredicate, cb.in(productRoot.get("productID")).value(subquery)));
            else return Optional.of(pricePredicate);
        }catch (JSONException err){
            return Optional.empty();
        }
    }

    private void sortProducts(Root<Product> product,
                              CriteriaQuery<Product> cq,
                              CriteriaBuilder criteriaBuilder,
                              ProductSorter pc) {

        if(pc.getOrder().equals("Price asc"))
            cq.orderBy(criteriaBuilder.asc(product.get("productPrice")));
        if(pc.getOrder().equals("Price desc"))
            cq.orderBy(criteriaBuilder.desc(product.get("productPrice")));

        if(pc.getOrder().equals("A-Z")) {
            Expression<String> name = product.get("productName");
            Expression<String> lowerProductName = criteriaBuilder.function("LOWER", String.class, name);
            cq.orderBy(criteriaBuilder.asc(lowerProductName));
        }
        if (pc.getOrder().equals("Rating")) {
            Join<Product, Review> reviews = product.join("productReviews", JoinType.LEFT);
            Expression<Double> averageRating = criteriaBuilder.avg(reviews.get("reviewScore"));
            cq.groupBy(product.get("productID")); // Group by the product_id
            Expression<Double> coalescedRating = criteriaBuilder.coalesce(averageRating, -10.0);
            cq.orderBy(criteriaBuilder.desc(coalescedRating));
        }

        if(pc.getOrder().equals("No. of reviews")) {
            Join<Product, Review> reviews = product.join("productReviews", JoinType.LEFT);
            Expression<Long> totalReviewNumber = criteriaBuilder.count(reviews.get("reviewScore"));
            cq.groupBy(product.get("productID")); // Group by the product_id
            cq.orderBy(criteriaBuilder.desc(totalReviewNumber));
        }
    }

    public CustomPageDTO<Product> getPaginatedProductsWithQuery(ProductSorter pc) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> product = cq.from(Product.class);

        //list for storing all the criteria predicates
        List<Predicate> finalPredicates = new ArrayList<>();

        //predicate for searching for products based on the name
        Predicate predicateName = cb.like(cb.upper(product.get("productName")), ("%" + pc.getQuery() + "%").toUpperCase());
        finalPredicates.add(cb.and(predicateName));

        //sort the results
        sortProducts(product, cq, cb, pc);

        //filter the results
        Optional<Predicate> filterPredicate = filterProducts(pc.getFilter(), cb, product, cq);
        filterPredicate.ifPresent(finalPredicates::add);

        cq.where(finalPredicates.toArray(new Predicate[0]));
        List<Product> result = em.createQuery(cq).getResultList();

        PagedListHolder<Product> page = new PagedListHolder<>(result);
        page.setPage(pc.getPageNumber() - 1);
        page.setPageSize(pc.getPageSize());

        CustomPageDTO<Product> dto = new CustomPageDTO<>(page.getPageList(), page.getPageSize(), page.getPageCount(), page.getNrOfElements());
        if(page.getPageCount() < pc.getPageNumber()) dto.setContent(new ArrayList<>());
        return dto;
    }
    public CustomPageDTO<Product> getPaginatedProductsWithoutQuery(ProductSorter pc) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> product = cq.from(Product.class);

        //list for storing all the criteria predicates
        List<Predicate> finalPredicates = new ArrayList<>();

        //sort the results
        sortProducts(product, cq, cb, pc);

        //filter the results
        Optional<Predicate> filterPredicate = filterProducts(pc.getFilter(), cb, product, cq);
        filterPredicate.ifPresent(finalPredicates::add);

        //filter by category
        String productCategoryString = productCategoryRepository.getReferenceById(pc.getProductCategory().getCategoryID()).getCategoryName();
        if(!Objects.equals(productCategoryString, "-All")) {
            // Define the category name parameter
            ParameterExpression<String> categoryNameParam = cb.parameter(String.class, "categoryName");
            // Join the Product entity with the ProductCategory entity
            Join<Product, ProductCategory> categoryJoin = product.join("productCategory");
            // Create the predicate to check if the product belongs to the specified category
            Predicate predicateCategory = cb.equal(categoryJoin.get("categoryName"), categoryNameParam);
            finalPredicates.add(predicateCategory);
        }

        cq.where(finalPredicates.toArray(new Predicate[0]));
        Query query = em.createQuery(cq);

        if(!Objects.equals(productCategoryString, "-All")) query.setParameter("categoryName", productCategoryString);
        List<Product> result = query.getResultList();
        PagedListHolder<Product> page = new PagedListHolder<>(result);
        page.setPage(pc.getPageNumber() - 1);
        page.setPageSize(pc.getPageSize());

        CustomPageDTO<Product> dto = new CustomPageDTO<>(page.getPageList(), page.getPageSize(), page.getPageCount(), page.getNrOfElements());
        if(page.getPageCount() < pc.getPageNumber()) dto.setContent(new ArrayList<>());
        return dto;
    }
}