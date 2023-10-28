package com.tech.techhubbackend.controller;

import com.tech.techhubbackend.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/image")
@CrossOrigin(origins = "*")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(path = "{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody Resource getImageById(@PathVariable UUID id){
        return imageService.getImageById(id);
    }
}
