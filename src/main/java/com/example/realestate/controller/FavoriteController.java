package com.example.realestate.controller;

import com.example.realestate.dto.FavoriteRequest;
import com.example.realestate.dto.FavoriteResponse;
import com.example.realestate.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<FavoriteResponse> addFavorite(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
            @RequestBody FavoriteRequest request) {

        Long userId = getUserIdFromUserDetails(userDetails);
        FavoriteResponse favorite = favoriteService.addFavorite(userId, request.getPropertyId());
        return ResponseEntity.ok(favorite);
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
            @PathVariable Long propertyId) {

        Long userId = getUserIdFromUserDetails(userDetails);
        favoriteService.removeFavorite(userId, propertyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> getUserFavorites(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        List<FavoriteResponse> favorites = favoriteService.getUserFavorites(userId);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/ids")
    public ResponseEntity<List<Long>> getUserFavoritePropertyIds(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        List<Long> favoriteIds = favoriteService.getUserFavoritePropertyIds(userId);
        return ResponseEntity.ok(favoriteIds);
    }

    private Long getUserIdFromUserDetails(org.springframework.security.core.userdetails.UserDetails userDetails) {
        // Implémentez cette méthode pour récupérer l'ID utilisateur
        // Par exemple, si votre User entity a getId():
        return ((com.example.realestate.entity.User) userDetails).getId();
    }
}