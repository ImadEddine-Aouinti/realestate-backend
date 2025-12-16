package com.example.realestate.controller;

import com.example.realestate.dto.ImageRequest;
import com.example.realestate.dto.PropertyRequest;
import com.example.realestate.dto.PropertyResponse;
import com.example.realestate.entity.Image;
import com.example.realestate.entity.Property;
import com.example.realestate.entity.User;
import com.example.realestate.repository.PropertyRepository;
import com.example.realestate.repository.UserRepository;
import com.example.realestate.service.PropertyService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public ResponseEntity<?> createProperty(@RequestBody PropertyRequest propertyRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            User currentUser = userRepository.findByEmail(currentUsername)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Property property = new Property();
            property.setTitle(propertyRequest.getTitle());
            property.setDescription(propertyRequest.getDescription());
            property.setPrice(propertyRequest.getPrice());
            property.setType(propertyRequest.getType());
            property.setStatus(propertyRequest.getStatus());
            property.setOwner(currentUser);
            property.setSurface(propertyRequest.getSurface());
            property.setBedrooms(propertyRequest.getBedrooms());
            property.setBathrooms(propertyRequest.getBathrooms());
            property.setRooms(propertyRequest.getRooms());
            property.setYearBuilt(propertyRequest.getYearBuilt());
            property.setAddress(propertyRequest.getAddress());
            property.setCity(propertyRequest.getCity());
            property.setPostalCode(propertyRequest.getPostalCode());
            property.setCountry(propertyRequest.getCountry());
            property.setHasParking(propertyRequest.getHasParking());
            property.setHasGarden(propertyRequest.getHasGarden());
            property.setHasPool(propertyRequest.getHasPool());
            property.setHasBalcony(propertyRequest.getHasBalcony());
            property.setHasElevator(propertyRequest.getHasElevator());
            property.setHasAirConditioning(propertyRequest.getHasAirConditioning());
            property.setHasHeating(propertyRequest.getHasHeating());
            property.setAdditionalFeatures(propertyRequest.getAdditionalFeatures());

            if (propertyRequest.getImages() != null && !propertyRequest.getImages().isEmpty()) {
                List<Image> images = new ArrayList<>();
                for (ImageRequest imageRequest : propertyRequest.getImages()) {
                    Image image = new Image();
                    image.setUrl(imageRequest.getUrl());
                    image.setAltText(imageRequest.getAltText());
                    image.setIsMain(imageRequest.getIsMain());
                    image.setProperty(property);
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