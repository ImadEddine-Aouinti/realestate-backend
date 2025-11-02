package com.example.realestate.controller;

import com.example.realestate.dto.PropertyResponse;
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
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyService propertyService;

    // GET - Récupérer toutes les propriétés
    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    // NOUVEAU - GET avec filtres
    @GetMapping("/filter")
    public ResponseEntity<List<PropertyResponse>> getPropertiesByFilters(
            @RequestParam(required = false) Property.PropertyType type,
            @RequestParam(required = false) Property.PropertyStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        try {
            System.out.println("🎯 Filtres reçus - Type: " + type +
                    ", Status: " + status +
                    ", MinPrice: " + minPrice +
                    ", MaxPrice: " + maxPrice);

            List<PropertyResponse> properties = propertyService.getPropertiesByFilters(
                    type, status, minPrice, maxPrice
            );

            System.out.println("✅ " + properties.size() + " propriétés trouvées avec ces filtres");
            return ResponseEntity.ok(properties);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du filtrage: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // GET - Récupérer une propriété par ID
    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    // POST - Créer une nouvelle propriété
    @PostMapping
    public ResponseEntity<?> createProperty(@RequestBody Property property) {
        try {
            // Récupérer l'utilisateur authentifié
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            User currentUser = userRepository.findByEmail(currentUsername)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Associer la propriété à l'utilisateur connecté
            property.setOwner(currentUser);

            // Sauvegarder la propriété
            Property savedProperty = propertyRepository.save(property);

            return ResponseEntity.ok(savedProperty);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la création: " + e.getMessage());
        }
    }

    // PUT - Mettre à jour une propriété
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProperty(@PathVariable Long id, @RequestBody Property propertyDetails) {
        try {
            Property property = propertyRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Propriété non trouvée"));

            // Mettre à jour les champs
            updatePropertyFields(property, propertyDetails);

            Property updatedProperty = propertyRepository.save(property);
            return ResponseEntity.ok(updatedProperty);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la mise à jour: " + e.getMessage());
        }
    }

    // DELETE - Supprimer une propriété
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