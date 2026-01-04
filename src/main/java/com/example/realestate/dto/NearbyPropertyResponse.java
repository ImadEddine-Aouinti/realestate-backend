package com.example.realestate.dto;

import com.example.realestate.entity.Property;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class NearbyPropertyResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Property.PropertyType type;
    private Property.PropertyStatus status;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private Double distance; // distance en km
    private Double latitude;
    private Double longitude;
    private String mainImageUrl;
    private LocalDateTime createdAt;

    // Informations propriétaire simplifiées
    private UserResponse owner;

    @Data
    public static class UserResponse {
        private Long id;
        private String nom;
        private String email;
    }
}