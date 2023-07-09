package com.tech.techhubbackend.service;

import com.tech.techhubbackend.exceptionhandling.exceptions.ImageNotFoundException;
import com.tech.techhubbackend.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Resource getImageById(UUID id) {

        if(!imageRepository.existsById(id)) throw new ImageNotFoundException();

        Path path;
        path = Paths.get(imageRepository.getReferenceById(id).getFilePath() + '/' + imageRepository.getReferenceById(id).getFilename());
        try {
            return new ByteArrayResource(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new ImageNotFoundException();
        }
    }
}
