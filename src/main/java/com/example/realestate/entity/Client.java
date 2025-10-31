package com.example.realestate.entity;

import com.example.realestate.entity.enums.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Client entity inheriting from User.
 * Represents property owners/renters/buyers.
 */
@Entity
@DiscriminatorValue("CLIENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client extends User {

    // No additional fields; role is set to ROLE_USER or custom if needed

    public Client(String nom, String email, String password, String telephone) {
        super(nom, email, password, telephone, Role.ROLE_USER, true, null, null);
    }
}