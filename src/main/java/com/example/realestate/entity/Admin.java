package com.example.realestate.entity;

import com.example.realestate.entity.enums.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Admin entity inheriting from User.
 * No additional fields; relies on role-based authorization.
 */
@Entity
@DiscriminatorValue("ADMIN")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin extends User {

    // No additional fields needed; role is set to ROLE_ADMIN in service layer during creation

    public Admin(String nom, String email, String password, String telephone) {
        super(nom, email, password, telephone, Role.ROLE_ADMIN, true, null, null);
    }
}