package com.example.realestate.service;

import com.example.realestate.dto.NearbyPropertiesRequest;
import com.example.realestate.dto.NearbyPropertyResponse;
import com.example.realestate.dto.PropertyResponse;
import com.example.realestate.entity.Property;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface PropertyService {

    // Méthodes existantes
    List<PropertyResponse> getAllProperties();
    List<PropertyResponse> getPropertiesByCurrentUser();
    PropertyResponse getPropertyById(Long id);
    List<PropertyResponse> getFavoriteProperties(Long userId);
    List<PropertyResponse> getPropertiesByFilters(Property.PropertyType type,
                                                  Property.PropertyStatus status,
                                                  BigDecimal minPrice,
                                                  BigDecimal maxPrice);
    List<PropertyResponse> getAvailableProperties();
    List<PropertyResponse> getUserFavorites(Long userId);
    PropertyResponse createProperty(Property property);

    // NOUVELLES MÉTHODES POUR LA RECHERCHE SPATIALE
    Page<NearbyPropertyResponse> findNearbyProperties(NearbyPropertiesRequest request);
    Page<NearbyPropertyResponse> findNearbyPropertiesForUser(Long userId, Double radius);
}