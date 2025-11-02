package com.example.realestate.repository;

import com.example.realestate.entity.Property;
import com.example.realestate.entity.Property.PropertyStatus;
import com.example.realestate.entity.Property.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {

    List<Property> findByStatus(PropertyStatus status);
    List<Property> findByType(PropertyType type);
    List<Property> findByOwnerId(Long ownerId);

    @Query("SELECT p FROM Property p WHERE " +
            "(:type IS NULL OR p.type = :type) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Property> findByFilters(@Param("type") PropertyType type,
                                 @Param("status") PropertyStatus status,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT p FROM Property p WHERE p.status = 'AVAILABLE' ORDER BY p.createdAt DESC")
    List<Property> findAvailableProperties();

    // NOUVELLE MÉTHODE - Charger une propriété avec owner et images
    @Query("SELECT p FROM Property p LEFT JOIN FETCH p.owner LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Property> findByIdWithOwnerAndImages(@Param("id") Long id);

    // OPTIONNEL - Charger toutes les propriétés avec owner et images
    @Query("SELECT p FROM Property p LEFT JOIN FETCH p.owner LEFT JOIN FETCH p.images")
    List<Property> findAllWithOwnerAndImages();
}