package com.ordering.jan.repository;

import com.ordering.jan.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // This allows the search bar to find items even with partial names
    List<Item> findByNameContainingIgnoreCase(String name);
 // Add this method to only find active products
    List<Item> findByActiveTrue();
        List<Item> findByActiveFalse();
        List<Item> findByActiveTrueAndNameContainingIgnoreCase(String name);
    }
