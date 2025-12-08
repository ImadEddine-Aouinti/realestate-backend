package com.example.realestate.service.impl;

import com.example.realestate.dto.LoginRequest;
import com.example.realestate.dto.JwtResponse;
import com.example.realestate.entity.User;
import com.example.realestate.service.AuthService;
import com.example.realestate.service.JwtService;
import com.example.realestate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public JwtResponse login(LoginRequest request) {
        System.out.println("🔐 Login attempt for: " + request.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // ✅ CORRECTION : Utiliser UserDetails de l'authentication
            org.springframework.security.core.userdetails.UserDetails userDetails =
                    (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();

            String jwtToken = jwtService.generateToken(userDetails);

            // Récupérer l'user entity pour la réponse
            User user = userService.findByEmail(request.getEmail());

            System.out.println("✅ Token generated successfully for: " + request.getEmail());
            System.out.println("Token preview: " + (jwtToken != null ? jwtToken.substring(0, Math.min(20, jwtToken.length())) + "..." : "NULL"));

            return new JwtResponse(jwtToken, user.getEmail(), user.getRole());

        } catch (Exception e) {
            System.err.println("❌ Login failed for " + request.getEmail() + ": " + e.getMessage());
            throw new RuntimeException("Erreur d'authentification: " + e.getMessage());
        }
    }
}