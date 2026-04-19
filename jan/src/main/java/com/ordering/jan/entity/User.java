package com.ordering.jan.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email; // Added this for registration

    @Column(nullable = false)
    private String role; // e.g., ROLE_ADMIN, ROLE_CASHIER, ROLE_STAFF

    private boolean enabled = true; // Added this to match your Controller logic

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Good for tracking when they joined
 // Add this field inside your User class
    @OneToOne(mappedBy = "systemAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private Labor laborProfile;
}