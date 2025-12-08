package com.example.realestate.service.impl;

import com.example.realestate.dto.PropertyResponse;
import com.example.realestate.entity.Property;
import com.example.realestate.entity.Property.PropertyStatus;
import com.example.realestate.entity.Property.PropertyType;
import com.example.realestate.repository.PropertyRepository;
import com.example.realestate.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;

    @Override
    public List<PropertyResponse> getAllProperties() {
        return propertyRepository.findAll().stream()
                .map(this::mapToPropertyResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PropertyResponse getPropertyById(Long id) {
        // Utilisez la nouvelle méthode qui charge owner et images
        Property property = propertyRepository.findByIdWithOwnerAndImages(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
        return mapToPropertyResponse(property);
    }

    @Override
    public List<PropertyResponse> getPropertiesByFilters(PropertyType type, PropertyStatus status,
                                                         BigDecimal minPrice, BigDecimal maxPrice) {
        List<Property> properties = propertyRepository.findByFilters(type, status, minPrice, maxPrice);
        return properties.stream()
                .map(this::mapToPropertyResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyResponse> getAvailableProperties() {
        return propertyRepository.findAvailableProperties().stream()
                .map(this::mapToPropertyResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyResponse> getUserFavorites(Long userId) {
        // Implémentation avec FavoriteService
        return List.of();
    }

    private PropertyResponse mapToPropertyResponse(Property property) {
        PropertyResponse response = new PropertyResponse();
        response.setId(property.getId());
        response.setTitle(property.getTitle());
        response.setDescription(property.getDescription());
        response.setPrice(property.getPrice());
        response.setType(property.getType());
        response.setStatus(property.getStatus());
        response.setCreatedAt(property.getCreatedAt());
        response.setUpdatedAt(property.getUpdatedAt());

        // MAPPER LES NOUVEAUX CHAMPS
        response.setSurface(property.getSurface());
        response.setBedrooms(property.getBedrooms());
        response.setBathrooms(property.getBathrooms());
        response.setRooms(property.getRooms());
        response.setYearBuilt(property.getYearBuilt());
        response.setAddress(property.getAddress());
        response.setCity(property.getCity());
        response.setPostalCode(property.getPostalCode());
        response.setCountry(property.getCountry());
        response.setHasParking(property.getHasParking());
        response.setHasGarden(property.getHasGarden());
        response.setHasPool(property.getHasPool());
        response.setHasBalcony(property.getHasBalcony());
        response.setHasElevator(property.getHasElevator());
        response.setHasAirConditioning(property.getHasAirConditioning());
        response.setHasHeating(property.getHasHeating());
        response.setAdditionalFeatures(property.getAdditionalFeatures());

        // Map owner - CORRIGÉ
        if (property.getOwner() != null) {
            PropertyResponse.UserResponse userResponse = new PropertyResponse.UserResponse();
            userResponse.setId(property.getOwner().getId());
            userResponse.setNom(property.getOwner().getNom()); // Utilisez getNom()
            userResponse.setEmail(property.getOwner().getEmail());
            userResponse.setTelephone(property.getOwner().getTelephone()); // Ajoutez le téléphone
            response.setOwner(userResponse);
        }

        // Map images
        if (property.getImages() != null) {
            response.setImages(property.getImages().stream()
                    .map(image -> {
                        PropertyResponse.ImageResponse imageResponse = new PropertyResponse.ImageResponse();
                        imageResponse.setId(image.getId());
                        imageResponse.setUrl(image.getUrl());
                        imageResponse.setIsMain(image.getIsMain());
                        return imageResponse;
                    })
                    .collect(Collectors.toList()));
        }

        return response;
    }
}