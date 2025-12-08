package com.example.realestate.controller;

import com.example.realestate.dto.ProfileUpdateRequest; // CORRECTION : ajouter .request
import com.example.realestate.dto.UserResponse; // CORRECTION : ajouter .response
import com.example.realestate.entity.User;
import com.example.realestate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(mapToUserResponse(user));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
            @RequestBody ProfileUpdateRequest request) {

        User currentUser = userService.findByEmail(userDetails.getUsername());
        currentUser.setNom(request.getNom());
        currentUser.setTelephone(request.getTelephone());

        User updatedUser = userService.updateUserProfile(currentUser);
        return ResponseEntity.ok(mapToUserResponse(updatedUser));
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setNom(user.getNom());
        response.setEmail(user.getEmail());
        response.setTelephone(user.getTelephone());
        response.setRole(user.getRole());
        response.setEnabled(user.getEnabled());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt()); // AJOUTER cette ligne
        return response;
    }
}