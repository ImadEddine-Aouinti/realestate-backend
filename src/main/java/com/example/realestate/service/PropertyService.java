package com.example.realestate.service;

import com.example.realestate.dto.PropertyResponse;
import com.example.realestate.entity.Property;
import com.example.realestate.entity.Property.PropertyStatus;
import com.example.realestate.entity.Property.PropertyType;

import java.math.BigDecimal;
import java.util.List;

public interface PropertyService {
    List<PropertyResponse> getAllProperties();
    PropertyResponse getPropertyById(Long id);
    List<PropertyResponse> getPropertiesByFilters(PropertyType type, PropertyStatus status,
                                                  BigDecimal minPrice, BigDecimal maxPrice);
    List<PropertyResponse> getAvailableProperties();
    List<PropertyResponse> getUserFavorites(Long userId);

}