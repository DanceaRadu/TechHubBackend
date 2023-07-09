package com.tech.techhubbackend.repository;

import com.tech.techhubbackend.model.ShoppingCartEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShoppingCartEntryRepository extends JpaRepository<ShoppingCartEntry, UUID> {
}
