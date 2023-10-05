package com.tech.techhubbackend.DTO.DTOs;

import com.tech.techhubbackend.model.ProductCategory;
import lombok.Data;

public @Data class ProductSorter {

    private ProductCategory productCategory;
    private String order;
    private String filter;
    private String query;
    private int pageNumber;
    private int pageSize;
}
