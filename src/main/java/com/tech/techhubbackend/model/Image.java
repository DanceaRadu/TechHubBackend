package com.tech.techhubbackend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Table(name = "images")
@Entity
public @Data class Image {

    @Id
    @Column(name = "image_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID imageID;

    @Column(name = "filename")
    private String filename;

    @Column(name = "file_path")
    private String filePath;
}
