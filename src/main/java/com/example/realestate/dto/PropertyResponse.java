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

    @Data
    public static class ImageResponse {
        private Long id;
        private String url;
        private Boolean isMain;
    }
}