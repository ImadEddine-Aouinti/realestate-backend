package com.example.realestate.controller;

import com.example.realestate.dto.PropertyResponse;
import com.example.realestate.entity.Property.PropertyStatus;
import com.example.realestate.entity.Property.PropertyType;
import com.example.realestate.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAllProperties(
            @RequestParam(required = false) PropertyType type,
            @RequestParam(required = false) PropertyStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        if (type != null || status != null || minPrice != null || maxPrice != null) {
            List<PropertyResponse> properties = propertyService.getPropertiesByFilters(type, status, minPrice, maxPrice);
            return ResponseEntity.ok(properties);
        }

        List<PropertyResponse> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable Long id) {
        PropertyResponse property = propertyService.getPropertyById(id);
        return ResponseEntity.ok(property);
    }

    @GetMapping("/available")
    public ResponseEntity<List<PropertyResponse>> getAvailableProperties() {
        List<PropertyResponse> properties = propertyService.getAvailableProperties();
        return ResponseEntity.ok(properties);
    }
}