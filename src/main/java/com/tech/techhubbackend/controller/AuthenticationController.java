package com.tech.techhubbackend.controller;

import com.tech.techhubbackend.DTO.DTOs.AuthenticationRequest;
import com.tech.techhubbackend.DTO.DTOs.AuthenticationResponse;
import com.tech.techhubbackend.DTO.DTOs.RegisterRequest;
import com.tech.techhubbackend.service.AuthenticationService;
import com.tech.techhubbackend.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/validate")
    public boolean validateToken() {
        return true;
    }

    @PostMapping("/mail/token")
    public void generateToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        authenticationService.createEmailToken(UUID.fromString(jwtService.extractID(token)));
    }

    @GetMapping("/mail/verify/{userID}")
    public boolean verifyMail(@PathVariable UUID userID) {
        return authenticationService.verifyEmail(userID);
    }
}