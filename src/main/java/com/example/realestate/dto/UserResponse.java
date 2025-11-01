package com.example.realestate.dto;

import com.example.realestate.entity.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String nom;
    private String email;
    private String telephone;
    private Role role;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // AJOUTER cette ligne
}