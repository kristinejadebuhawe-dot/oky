package com.ordering.jan.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.ordering.jan.entity.User;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // This finds a user by their username for the duplicate check
    Optional<User> findByUsername(String username);
    
    // This finds a user by their email for the duplicate check
    Optional<User> findByEmail(String email);
}