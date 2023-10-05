package com.tech.techhubbackend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tech.techhubbackend.auth.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public @Data class User implements UserDetails {

    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userID;

    @Column(name = "username", nullable = false, unique = true)
    private String _username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "is_verified")
    private boolean isVerified;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Image profileImage;

    @JsonManagedReference(value = "User shopping cart entry")
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<ShoppingCartEntry> shoppingCartEntries;

    @JsonManagedReference(value = "reviewer")
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "reviewer", cascade = CascadeType.REMOVE)
    private List<Review> userReviews;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return userID.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isVerified;
    }
}