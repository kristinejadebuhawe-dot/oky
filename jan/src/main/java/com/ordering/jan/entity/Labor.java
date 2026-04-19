package com.ordering.jan.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Labor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String position;
    private Double dailyWage;
    private String contactNumber;
    private boolean active = true;

    // --- NEXUS CONNECTION ---
    @OneToOne
    @JoinColumn(name = "user_id") // Creates a foreign key in the labor table
    private User systemAccount; 
}