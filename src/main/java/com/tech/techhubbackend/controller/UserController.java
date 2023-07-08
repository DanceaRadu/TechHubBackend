package com.tech.techhubbackend.controller;

import com.tech.techhubbackend.DTO.DTOs.UserDetailsDTO;
import com.tech.techhubbackend.service.JwtService;
import com.tech.techhubbackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/user")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class UserController {

    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping
    private UserDetailsDTO getUserDetails(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return userService.getUserDetails(UUID.fromString(jwtService.extractID(token)));
    }

    @GetMapping(path = "profilepicture",  produces = MediaType.IMAGE_JPEG_VALUE)
    private @ResponseBody ResponseEntity<byte[]> getUserPicture(HttpServletRequest request){
        String token = request.getHeader("Authorization").substring(7);
        System.out.println(UUID.fromString(jwtService.extractID(token)));
        byte[] image;
        try {
            image = userService.getUserPicture(UUID.fromString(jwtService.extractID(token))).getContentAsByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(image, headers, 200);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
