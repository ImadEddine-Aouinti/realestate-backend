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
        if (favoriteRepository.existsByUserIdAndPropertyId(userId, propertyId)) {
            throw new RuntimeException("Cette propriété est déjà dans vos favoris");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Propriété non trouvée"));

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
        if (!favoriteRepository.existsByUserIdAndPropertyId(userId, propertyId)) {
            throw new RuntimeException("Cette propriété n'est pas dans vos favoris");
        }
        favoriteRepository.deleteByUserIdAndPropertyId(userId, propertyId);
    }

    @Override
    public List<FavoriteResponse> getUserFavorites(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        return favorites.stream()
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

        // Récupérer la propriété avec toutes ses informations
        Property property = favorite.getProperty();
        PropertyResponse propertyResponse = new PropertyResponse();

        propertyResponse.setId(property.getId());
        propertyResponse.setTitle(property.getTitle());
        propertyResponse.setDescription(property.getDescription());
        propertyResponse.setPrice(property.getPrice());
        propertyResponse.setType(property.getType());
        propertyResponse.setStatus(property.getStatus());
        propertyResponse.setSurface(property.getSurface());
        propertyResponse.setBedrooms(property.getBedrooms());
        propertyResponse.setBathrooms(property.getBathrooms());
        propertyResponse.setAddress(property.getAddress());
        propertyResponse.setCity(property.getCity());
        propertyResponse.setCountry(property.getCountry());

        // Ajouter les images de la propriété
        if (property.getImages() != null && !property.getImages().isEmpty()) {
            List<PropertyResponse.ImageResponse> imageResponses = property.getImages().stream()
                    .map(image -> {
                        PropertyResponse.ImageResponse imageResponse = new PropertyResponse.ImageResponse();
                        imageResponse.setId(image.getId());
                        imageResponse.setUrl(image.getUrl());
                        imageResponse.setAltText(image.getAltText());
                        imageResponse.setIsMain(image.getIsMain());
                        return imageResponse;
                    })
                    .collect(Collectors.toList());
            propertyResponse.setImages(imageResponses);

            // Ajouter l'URL de l'image principale
            propertyResponse.setMainImageUrl(property.getMainImageUrl());
        }

        response.setProperty(propertyResponse);

        return response;
    }
}