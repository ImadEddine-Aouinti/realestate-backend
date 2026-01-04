package com.example.realestate.controller;

import com.example.realestate.dto.ImageRequest;
import com.example.realestate.dto.NearbyPropertiesRequest;
import com.example.realestate.dto.NearbyPropertyResponse;
import com.example.realestate.dto.PropertyRequest;
import com.example.realestate.dto.PropertyResponse;
import com.example.realestate.entity.Image;
import com.example.realestate.entity.Property;
import com.example.realestate.entity.User;
import com.example.realestate.repository.PropertyRepository;
import com.example.realestate.repository.UserRepository;
import com.example.realestate.service.PropertyService;
import com.example.realestate.util.GeometryUtil;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point; // NOUVEAU IMPORT
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyService propertyService;

    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    @GetMapping("/my-properties")
    public ResponseEntity<List<PropertyResponse>> getMyProperties() {
        try {
            List<PropertyResponse> properties = propertyService.getPropertiesByCurrentUser();
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<PropertyResponse>> getFavoriteProperties() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            User currentUser = userRepository.findByEmail(currentUsername)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            List<PropertyResponse> favoriteProperties = propertyService.getFavoriteProperties(currentUser.getId());
            return ResponseEntity.ok(favoriteProperties);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<PropertyResponse>> getPropertiesByFilters(
            @RequestParam(required = false) Property.PropertyType type,
            @RequestParam(required = false) Property.PropertyStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        try {
            List<PropertyResponse> properties = propertyService.getPropertiesByFilters(
                    type, status, minPrice, maxPrice
            );
            return ResponseEntity.ok(properties);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    // NOUVEAUX ENDPOINTS POUR LA RECHERCHE SPATIALE

    @PostMapping("/nearby")
    public ResponseEntity<Page<NearbyPropertyResponse>> getNearbyProperties(
            @RequestBody NearbyPropertiesRequest request) {
        return ResponseEntity.ok(propertyService.findNearbyProperties(request));
    }

    @GetMapping("/nearby/me")
    public ResponseEntity<Page<NearbyPropertyResponse>> getNearbyPropertiesForUser(
            @RequestParam(required = false) Double radius) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            User currentUser = userRepository.findByEmail(currentUsername)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Page<NearbyPropertyResponse> properties = propertyService.findNearbyPropertiesForUser(
                    currentUser.getId(),
                    radius
            );
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Dans la méthode updateUserLocation du PropertyController
    @PostMapping("/user/location")
    public ResponseEntity<?> updateUserLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            User currentUser = userRepository.findByEmail(currentUsername)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Utiliser GeometryUtil pour créer le point avec le bon SRID
            Point point = GeometryUtil.createPoint(longitude, latitude);
            currentUser.setLocation(point);
            userRepository.save(currentUser);

            return ResponseEntity.ok("Localisation mise à jour avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createProperty(@RequestBody PropertyRequest propertyRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            User currentUser = userRepository.findByEmail(currentUsername)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Property property = Property.builder()
                    .title(propertyRequest.getTitle())
                    .description(propertyRequest.getDescription())
                    .price(propertyRequest.getPrice())
                    .type(propertyRequest.getType())
                    .status(propertyRequest.getStatus())
                    .owner(currentUser)
                    .surface(propertyRequest.getSurface())
                    .bedrooms(propertyRequest.getBedrooms())
                    .bathrooms(propertyRequest.getBathrooms())
                    .rooms(propertyRequest.getRooms())
                    .yearBuilt(propertyRequest.getYearBuilt())
                    .address(propertyRequest.getAddress())
                    .city(propertyRequest.getCity())
                    .postalCode(propertyRequest.getPostalCode())
                    .country(propertyRequest.getCountry())
                    .hasParking(propertyRequest.getHasParking())
                    .hasGarden(propertyRequest.getHasGarden())
                    .hasPool(propertyRequest.getHasPool())
                    .hasBalcony(propertyRequest.getHasBalcony())
                    .hasElevator(propertyRequest.getHasElevator())
                    .hasAirConditioning(propertyRequest.getHasAirConditioning())
                    .hasHeating(propertyRequest.getHasHeating())
                    .additionalFeatures(propertyRequest.getAdditionalFeatures())
                    .build();

            // IMPORTANT: NE PAS créer d'images ici - elles seront uploadées séparément
            // Les URLs d'images seront vides initialement
            if (propertyRequest.getImages() != null && !propertyRequest.getImages().isEmpty()) {
                List<Image> images = new ArrayList<>();
                for (ImageRequest imageRequest : propertyRequest.getImages()) {
                    Image image = Image.builder()
                            .url("") // URL vide - sera remplie après upload
                            .altText(imageRequest.getAltText())
                            .isMain(imageRequest.getIsMain())
                            .property(property)
                            .build();
                    images.add(image);
                }
                property.setImages(images);
            }

            Property savedProperty = propertyRepository.save(property);

            PropertyResponse response = propertyService.getPropertyById(savedProperty.getId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la création: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProperty(@PathVariable Long id, @RequestBody Property propertyDetails) {
        try {
            Property property = propertyRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Propriété non trouvée"));

            updatePropertyFields(property, propertyDetails);

            Property updatedProperty = propertyRepository.save(property);
            return ResponseEntity.ok(updatedProperty);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la mise à jour: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProperty(@PathVariable Long id) {
        try {
            if (!propertyRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            propertyRepository.deleteById(id);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    private void updatePropertyFields(Property property, Property propertyDetails) {
        if (propertyDetails.getTitle() != null) property.setTitle(propertyDetails.getTitle());
        if (propertyDetails.getDescription() != null) property.setDescription(propertyDetails.getDescription());
        if (propertyDetails.getPrice() != null) property.setPrice(propertyDetails.getPrice());
        if (propertyDetails.getType() != null) property.setType(propertyDetails.getType());
        if (propertyDetails.getStatus() != null) property.setStatus(propertyDetails.getStatus());
        if (propertyDetails.getSurface() != null) property.setSurface(propertyDetails.getSurface());
        if (propertyDetails.getBedrooms() != null) property.setBedrooms(propertyDetails.getBedrooms());
        if (propertyDetails.getBathrooms() != null) property.setBathrooms(propertyDetails.getBathrooms());
        if (propertyDetails.getRooms() != null) property.setRooms(propertyDetails.getRooms());
        if (propertyDetails.getYearBuilt() != null) property.setYearBuilt(propertyDetails.getYearBuilt());
        if (propertyDetails.getAddress() != null) property.setAddress(propertyDetails.getAddress());
        if (propertyDetails.getCity() != null) property.setCity(propertyDetails.getCity());
        if (propertyDetails.getPostalCode() != null) property.setPostalCode(propertyDetails.getPostalCode());
        if (propertyDetails.getCountry() != null) property.setCountry(propertyDetails.getCountry());
        if (propertyDetails.getHasParking() != null) property.setHasParking(propertyDetails.getHasParking());
        if (propertyDetails.getHasGarden() != null) property.setHasGarden(propertyDetails.getHasGarden());
        if (propertyDetails.getHasPool() != null) property.setHasPool(propertyDetails.getHasPool());
        if (propertyDetails.getHasBalcony() != null) property.setHasBalcony(propertyDetails.getHasBalcony());
        if (propertyDetails.getHasElevator() != null) property.setHasElevator(propertyDetails.getHasElevator());
        if (propertyDetails.getHasAirConditioning() != null) property.setHasAirConditioning(propertyDetails.getHasAirConditioning());
        if (propertyDetails.getHasHeating() != null) property.setHasHeating(propertyDetails.getHasHeating());
        if (propertyDetails.getAdditionalFeatures() != null) property.setAdditionalFeatures(propertyDetails.getAdditionalFeatures());
    }
}