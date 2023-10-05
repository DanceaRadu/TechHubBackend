package com.tech.techhubbackend.DTO.DTOs;

import lombok.Data;

import java.util.List;

public @Data class CustomPageDTO<T> {

    private List<T> content;
    private int pageSize;
    private int numberOfPages;
    private int totalElements;

    public CustomPageDTO() {
    }

    public CustomPageDTO(List<T> content, int pageSize, int numberOfPages, int totalElements) {
        this.content = content;
        this.pageSize = pageSize;
        this.numberOfPages = numberOfPages;
        this.totalElements = totalElements;
    }
}
