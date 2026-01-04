package com.example.realestate.dto;

import lombok.Data;

@Data
public class NearbyPropertiesRequest {
    private Double latitude;
    private Double longitude;
    private Double radius = 10.0; // Rayon par défaut 10km
    private Integer page = 0;
    private Integer size = 20;
}