package com.example.realestate.dto;

// PropertyRequest.java
import com.example.realestate.entity.Property;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PropertyRequest {
    @NotBlank
    private String title;

    private String description;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Property.PropertyType type;

    @NotNull
    private Property.PropertyStatus status;

    @NotNull
    private Double surface;

    private Integer bedrooms;
    private Integer bathrooms;
    private Integer rooms;
    private Integer yearBuilt;

    @NotBlank
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

    private List<ImageRequest> images;
}
