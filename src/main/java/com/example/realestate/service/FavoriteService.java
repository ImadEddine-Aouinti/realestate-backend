package com.example.realestate.service;

import com.example.realestate.dto.FavoriteResponse;

import java.util.List;

public interface FavoriteService {
    FavoriteResponse addFavorite(Long userId, Long propertyId);
    void removeFavorite(Long userId, Long propertyId);
    List<FavoriteResponse> getUserFavorites(Long userId);
    List<Long> getUserFavoritePropertyIds(Long userId);
    boolean isPropertyInFavorites(Long userId, Long propertyId);
}