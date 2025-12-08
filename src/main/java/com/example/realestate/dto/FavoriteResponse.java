package com.example.realestate.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FavoriteResponse {
    private Long id;
    private Long propertyId;
    private Long userId;
    private LocalDateTime addedAt;
    private PropertyResponse property;
}