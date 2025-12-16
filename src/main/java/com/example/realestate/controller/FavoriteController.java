package com.example.realestate.controller;

import com.example.realestate.dto.FavoriteRequest;
import com.example.realestate.dto.FavoriteResponse;
import com.example.realestate.entity.User;
import com.example.realestate.repository.UserRepository;
import com.example.realestate.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<FavoriteResponse> addFavorite(@RequestBody FavoriteRequest request) {
        User currentUser = getCurrentUser();
        FavoriteResponse favorite = favoriteService.addFavorite(currentUser.getId(), request.getPropertyId());
        return ResponseEntity.ok(favorite);
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long propertyId) {
        User currentUser = getCurrentUser();
        favoriteService.removeFavorite(currentUser.getId(), propertyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> getUserFavorites() {
        User currentUser = getCurrentUser();
        List<FavoriteResponse> favorites = favoriteService.getUserFavorites(currentUser.getId());
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/ids")
    public ResponseEntity<List<Long>> getUserFavoritePropertyIds() {
        User currentUser = getCurrentUser();
        List<Long> favoriteIds = favoriteService.getUserFavoritePropertyIds(currentUser.getId());
        return ResponseEntity.ok(favoriteIds);
    }

    @GetMapping("/check/{propertyId}")
    public ResponseEntity<Boolean> isPropertyFavorite(@PathVariable Long propertyId) {
        User currentUser = getCurrentUser();
        boolean isFavorite = favoriteService.isPropertyInFavorites(currentUser.getId(), propertyId);
        return ResponseEntity.ok(isFavorite);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}