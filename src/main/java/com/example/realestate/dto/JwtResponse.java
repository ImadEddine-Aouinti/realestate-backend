package com.example.realestate.dto;

import com.example.realestate.entity.enums.Role;
import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private Role role;
    private String redirectUrl;

    public JwtResponse(String token, String email, Role role) {
        this.token = token;
        this.email = email;
        this.role = role;

        // Définir l'URL de redirection selon le rôle
        if (role == Role.ROLE_ADMIN) {
            this.redirectUrl = "/admin/dashboard";
        } else {
            this.redirectUrl = "/client/dashboard";
        }
    }
}
