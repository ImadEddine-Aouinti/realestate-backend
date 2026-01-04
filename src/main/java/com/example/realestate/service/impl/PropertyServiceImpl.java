package com.example.realestate.service.impl;

import com.example.realestate.dto.NearbyPropertiesRequest;
import com.example.realestate.dto.NearbyPropertyResponse;
import com.example.realestate.dto.PropertyResponse;
import com.example.realestate.entity.Property;
import com.example.realestate.entity.User;
import com.example.realestate.repository.PropertyRepository;
import com.example.realestate.repository.UserRepository;
import com.example.realestate.service.FavoriteService;
import com.example.realestate.service.PropertyService;
import com.example.realestate.util.GeometryUtil;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final FavoriteService favoriteService;

    @Override
    public List<PropertyResponse> getAllProperties() {
        List<Property> properties = propertyRepository.findAllWithOwnerAndImages();
        return properties.stream()
                .map(this::mapToPropertyResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyResponse> getPropertiesByCurrentUser() {
        // Récupérer l'utilisateur connecté
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupérer les propriétés de cet utilisateur
        List<Property> properties = propertyRepository.findByOwnerId(currentUser.getId());

        return properties.stream()
                .map(this::mapToPropertyResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PropertyResponse getPropertyById(Long id) {
        Property property = propertyRepository.findByIdWithOwnerAndImages(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
        return mapToPropertyResponse(property);
    }

    @Override
    public List<PropertyResponse> getFavoriteProperties(Long userId) {
        List<Long> favoriteIds = favoriteService.getUserFavoritePropertyIds(userId);

        if (favoriteIds.isEmpty()) {
            return List.of();
        }

        List<Property> properties = propertyRepository.findAllById(favoriteIds);
        return properties.stream()
                .map(this::mapToPropertyResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyResponse> getPropertiesByFilters(
            Property.PropertyType type,
            Property.PropertyStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice) {

        List<Property> properties = propertyRepository.findByFilters(type, status, minPrice, maxPrice);
        return properties.stream()
                .map(this::mapToPropertyResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyResponse> getAvailableProperties() {
        List<Property> properties = propertyRepository.findByStatus(Property.PropertyStatus.AVAILABLE);
        return properties.stream()
                .map(this::mapToPropertyResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyResponse> getUserFavorites(Long userId) {
        return List.of();
    }

    @Override
    public PropertyResponse createProperty(Property property) {
        Property savedProperty = propertyRepository.save(property);
        return mapToPropertyResponse(savedProperty);
    }

    // NOUVELLES MÉTHODES POUR LA RECHERCHE SPATIALE

    // Dans la méthode findNearbyProperties de PropertyServiceImpl
    @Override
    public Page<NearbyPropertyResponse> findNearbyProperties(NearbyPropertiesRequest request) {
        // Créer le point géographique avec le bon SRID
        Point point = GeometryUtil.createPoint(request.getLongitude(), request.getLatitude());

        // Exécuter la requête
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Object[]> results = propertyRepository.findNearbyProperties(
                point,
                request.getRadius(),
                pageable
        );

        // Convertir les résultats en DTO
        List<NearbyPropertyResponse> properties = results.getContent().stream()
                .map(this::convertToNearbyDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(properties, pageable, results.getTotalElements());
    }

    @Override
    public Page<NearbyPropertyResponse> findNearbyPropertiesForUser(Long userId, Double radius) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (user.getLocation() == null) {
            throw new RuntimeException("L'utilisateur n'a pas de localisation définie");
        }

        NearbyPropertiesRequest request = new NearbyPropertiesRequest();
        request.setLatitude(user.getLocation().getY());
        request.setLongitude(user.getLocation().getX());
        request.setRadius(radius != null ? radius : 10.0); // Rayon par défaut 10km
        request.setPage(0);
        request.setSize(20);

        return findNearbyProperties(request);
    }

    private NearbyPropertyResponse convertToNearbyDTO(Object[] result) {
        Property property = (Property) result[0];
        Double distance = (Double) result[1];

        NearbyPropertyResponse dto = new NearbyPropertyResponse();
        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setPrice(property.getPrice());
        dto.setType(property.getType());
        dto.setStatus(property.getStatus());
        dto.setAddress(property.getAddress());
        dto.setCity(property.getCity());
        dto.setPostalCode(property.getPostalCode());
        dto.setCountry(property.getCountry());
        dto.setDistance(distance);
        dto.setLatitude(property.getLatitude());
        dto.setLongitude(property.getLongitude());
        dto.setCreatedAt(property.getCreatedAt());

        // Récupérer l'image principale
        dto.setMainImageUrl(property.getMainImageUrl());

        // Informations sur le propriétaire
        if (property.getOwner() != null) {
            NearbyPropertyResponse.UserResponse ownerResponse = new NearbyPropertyResponse.UserResponse();
            ownerResponse.setId(property.getOwner().getId());
            ownerResponse.setNom(property.getOwner().getNom());
            ownerResponse.setEmail(property.getOwner().getEmail());
            dto.setOwner(ownerResponse);
        }

        return dto;
    }

    private PropertyResponse mapToPropertyResponse(Property property) {
        PropertyResponse response = new PropertyResponse();
        response.setId(property.getId());
        response.setTitle(property.getTitle());
        response.setDescription(property.getDescription());
        response.setPrice(property.getPrice());
        response.setType(property.getType());
        response.setStatus(property.getStatus());
        response.setCreatedAt(property.getCreatedAt());
        response.setUpdatedAt(property.getUpdatedAt());

        response.setSurface(property.getSurface());
        response.setBedrooms(property.getBedrooms());
        response.setBathrooms(property.getBathrooms());
        response.setRooms(property.getRooms());
        response.setYearBuilt(property.getYearBuilt());
        response.setAddress(property.getAddress());
        response.setCity(property.getCity());
        response.setPostalCode(property.getPostalCode());
        response.setCountry(property.getCountry());
        response.setHasParking(property.getHasParking());
        response.setHasGarden(property.getHasGarden());
        response.setHasPool(property.getHasPool());
        response.setHasBalcony(property.getHasBalcony());
        response.setHasElevator(property.getHasElevator());
        response.setHasAirConditioning(property.getHasAirConditioning());
        response.setHasHeating(property.getHasHeating());
        response.setAdditionalFeatures(property.getAdditionalFeatures());

        if (property.getOwner() != null) {
            PropertyResponse.UserResponse ownerResponse = new PropertyResponse.UserResponse();
            ownerResponse.setId(property.getOwner().getId());
            ownerResponse.setNom(property.getOwner().getNom());
            ownerResponse.setEmail(property.getOwner().getEmail());
            ownerResponse.setTelephone(property.getOwner().getTelephone());
            response.setOwner(ownerResponse);
        }

        if (property.getImages() != null && !property.getImages().isEmpty()) {
            List<PropertyResponse.ImageResponse> imageResponses = property.getImages().stream()
                    .map(image -> {
                        PropertyResponse.ImageResponse imageResponse = new PropertyResponse.ImageResponse();
                        imageResponse.setId(image.getId());
                        imageResponse.setUrl(image.getUrl());
                        imageResponse.setAltText(image.getAltText());
                        imageResponse.setIsMain(image.getIsMain());
                        return imageResponse;
                    })
                    .collect(Collectors.toList());
            response.setImages(imageResponses);
        }

        return response;
    }
}