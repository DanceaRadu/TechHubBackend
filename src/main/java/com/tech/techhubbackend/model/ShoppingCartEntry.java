package com.tech.techhubbackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "shopping_cart_entries")
public @Data class ShoppingCartEntry {

    @Id
    @Column(name = "shopping_cart_entry_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID shoppingCartEntryID;

    @JsonBackReference("User shopping cart entry")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @JsonBackReference("Product shopping cart entry")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private int quantity;
}
