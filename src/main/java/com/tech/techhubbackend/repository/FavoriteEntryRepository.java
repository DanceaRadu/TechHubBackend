package com.tech.techhubbackend.repository;

import com.tech.techhubbackend.model.FavoriteEntry;
import com.tech.techhubbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FavoriteEntryRepository extends JpaRepository<FavoriteEntry, UUID> {
    List<FavoriteEntry> getFavoriteEntriesByUser(User user);
}
