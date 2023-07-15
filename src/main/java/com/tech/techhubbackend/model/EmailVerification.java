package com.tech.techhubbackend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "email_verification")
public @Data class EmailVerification {

    @Id
    @Column(name = "email_verification_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID emailVerificationID;

    @OneToOne()
    @JoinColumn(unique = true)
    private User user;
}