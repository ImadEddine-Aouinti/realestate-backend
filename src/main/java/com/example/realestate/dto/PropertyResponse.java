package com.example.realestate.dto;

import com.example.realestate.entity.Property;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PropertyResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Property.PropertyType type;
    private Property.PropertyStatus status;
    private UserResponse owner;
    private List<ImageResponse> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // NOUVEAUX CHAMPS
    private Double surface;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer rooms;
    private Integer yearBuilt;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private Boolean hasParking;
    private Boolean hasGarden;
    private Boolean hasPool;
    private Boolean hasBalcony;
    private Boolean hasElevator;
    private Boolean hasAirConditioning;
    private Boolean hasHeating;
    private String additionalFeatures;

    @Data
    public static class ImageResponse {
        private Long id;
        private String url;
        private Boolean isMain;
    }

    @Data
    public static class UserResponse {
        private Long id;
        private String nom; // Utilisez 'nom' au lieu de firstName/lastName
        private String email;
        private String telephone;
        // Ajoutez d'autres champs si nécessaire
    }
}