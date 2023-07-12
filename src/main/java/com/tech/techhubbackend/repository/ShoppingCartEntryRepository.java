package com.tech.techhubbackend.repository;

import com.tech.techhubbackend.model.Product;
import com.tech.techhubbackend.model.ShoppingCartEntry;
import com.tech.techhubbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartEntryRepository extends JpaRepository<ShoppingCartEntry, UUID> {
    Optional<ShoppingCartEntry> getShoppingCartEntryByProductAndUser(Product p, User u);
}
