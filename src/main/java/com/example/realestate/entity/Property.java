package com.example.realestate.entity;

import com.example.realestate.entity.enums.PropertyStatus;
import com.example.realestate.entity.enums.PropertyType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Property entity representing real estate listings.
 * Includes geospatial fields for distance calculations.
 */
@Entity
@Table(name = "properties")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false, length = 200)
    private String titre;

    @NotBlank(message = "Description is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    @DecimalMax(value = "999999999.99", message = "Price too high")
    @Column(nullable = false, precision = 10, scale = 2)
    private Double prix;

    @NotBlank(message = "Address is required")
    @Column(nullable = false, length = 255)
    private String adresse;

    @NotBlank(message = "City is required")
    @Column(nullable = false, length = 100)
    private String ville;

    @NotNull(message = "Property type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType type;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus status = PropertyStatus.DISPONIBLE;

    @DecimalMin(value = "-90.0", message = "Invalid latitude")
    @DecimalMax(value = "90.0", message = "Invalid latitude")
    @Column(precision = 10, scale = 8)
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Invalid longitude")
    @DecimalMax(value = "180.0", message = "Invalid longitude")
    @Column(precision = 11, scale = 8)
    private Double longitude;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;  // Assuming Client as owner

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Image> images;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    /**
     * Calculates the Haversine distance between this property and another location.
     * @param lat2 Latitude of the other point
     * @param lon2 Longitude of the other point
     * @return Distance in kilometers
     */
    public double calculateDistance(double lat2, double lon2) {
        if (latitude == null || longitude == null) {
            throw new IllegalStateException("Property location not set");
        }
        final int R = 6371; // Earth's radius in km
        double lat1 = Math.toRadians(latitude);
        double lon1 = Math.toRadians(longitude);
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(lat1) * Math.cos(lat1) *
                Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Updates the property status with validation.
     * @param newStatus The new status to set
     * @throws IllegalStateException if invalid transition
     */
    public void updateStatus(PropertyStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        // Example business rule: Cannot set to VENDU if already LOUE
        if (status == PropertyStatus.LOUE && newStatus == PropertyStatus.VENDU) {
            throw new IllegalStateException("Cannot sell a rented property");
        }
        this.status = newStatus;
    }
}