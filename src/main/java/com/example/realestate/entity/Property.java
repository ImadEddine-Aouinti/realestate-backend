package com.example.realestate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus status;

    // NOUVEAUX CHAMPS AJOUTÉS
    @Column(nullable = false)
    private Double surface; // Surface en m²

    private Integer bedrooms; // Nombre de chambres

    private Integer bathrooms; // Nombre de salles de bain

    private Integer rooms; // Nombre total de pièces

    private Integer yearBuilt; // Année de construction

    @Column(nullable = false)
    private String address; // Adresse complète

    private String city; // Ville

    private String postalCode; // Code postal

    private String country; // Pays

    // Caractéristiques supplémentaires
    private Boolean hasParking;
    private Boolean hasGarden;
    private Boolean hasPool;
    private Boolean hasBalcony;
    private Boolean hasElevator;
    private Boolean hasAirConditioning;
    private Boolean hasHeating;

    @Column(columnDefinition = "TEXT")
    private String additionalFeatures; // Caractéristiques supplémentaires

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Favorite> favorites = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum PropertyType {
        APARTMENT("Appartement"),
        HOUSE("Maison"),
        VILLA("Villa"),
        OFFICE("Bureau"),
        COMMERCIAL("Commercial"),
        LAND("Terrain");

        private final String displayName;

        PropertyType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum PropertyStatus {
        AVAILABLE("Disponible"),
        PENDING("En attente"),
        RENTED("Loué"),
        SOLD("Vendu");

        private final String displayName;

        PropertyStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Méthode utilitaire pour obtenir l'image principale
    public String getMainImageUrl() {
        return this.images.stream()
                .filter(Image::getIsMain)
                .findFirst()
                .map(Image::getUrl)
                .orElse(this.images.isEmpty() ? null : this.images.get(0).getUrl());
    }
}