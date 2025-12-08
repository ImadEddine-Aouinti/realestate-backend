package com.example.realestate.service.impl;

import com.example.realestate.dto.FavoriteResponse;
import com.example.realestate.dto.PropertyResponse;
import com.example.realestate.entity.Favorite;
import com.example.realestate.entity.Property;
import com.example.realestate.entity.User;
import com.example.realestate.repository.FavoriteRepository;
import com.example.realestate.repository.PropertyRepository;
import com.example.realestate.repository.UserRepository;
import com.example.realestate.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    @Override
    @Transactional
    public FavoriteResponse addFavorite(Long userId, Long propertyId) {
        // Vérifier si déjà en favoris
        if (favoriteRepository.existsByUserIdAndPropertyId(userId, propertyId)) {
            throw new RuntimeException("Property already in favorites");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        Favorite favorite = Favorite.builder()
                .user(user)
                .property(property)
                .build();

        Favorite savedFavorite = favoriteRepository.save(favorite);
        return mapToFavoriteResponse(savedFavorite);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long propertyId) {
        favoriteRepository.deleteByUserIdAndPropertyId(userId, propertyId);
    }

    @Override
    public List<FavoriteResponse> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(this::mapToFavoriteResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getUserFavoritePropertyIds(Long userId) {
        return favoriteRepository.findFavoritePropertyIdsByUserId(userId);
    }

    @Override
    public boolean isPropertyInFavorites(Long userId, Long propertyId) {
        return favoriteRepository.existsByUserIdAndPropertyId(userId, propertyId);
    }

    private FavoriteResponse mapToFavoriteResponse(Favorite favorite) {
        FavoriteResponse response = new FavoriteResponse();
        response.setId(favorite.getId());
        response.setPropertyId(favorite.getProperty().getId());
        response.setUserId(favorite.getUser().getId());
        response.setAddedAt(favorite.getAddedAt());

        // Map property details
        PropertyResponse propertyResponse = new PropertyResponse();
        propertyResponse.setId(favorite.getProperty().getId());
        propertyResponse.setTitle(favorite.getProperty().getTitle());
        propertyResponse.setDescription(favorite.getProperty().getDescription());
        propertyResponse.setPrice(favorite.getProperty().getPrice());
        propertyResponse.setType(favorite.getProperty().getType());
        propertyResponse.setStatus(favorite.getProperty().getStatus());

        response.setProperty(propertyResponse);

        return response;
    }
}