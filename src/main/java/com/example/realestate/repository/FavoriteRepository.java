package com.example.realestate.repository;

import com.example.realestate.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndPropertyId(Long userId, Long propertyId);
    List<Favorite> findByUserId(Long userId);
    boolean existsByUserIdAndPropertyId(Long userId, Long propertyId);
    void deleteByUserIdAndPropertyId(Long userId, Long propertyId);

    @Query("SELECT f.property.id FROM Favorite f WHERE f.user.id = :userId")
    List<Long> findFavoritePropertyIdsByUserId(@Param("userId") Long userId);
}