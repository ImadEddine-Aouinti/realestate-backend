// ImageController.java
package com.example.realestate.controller;

import com.example.realestate.dto.ImageDTO;
import com.example.realestate.entity.Image;
import com.example.realestate.entity.Property;
import com.example.realestate.repository.ImageRepository;
import com.example.realestate.repository.PropertyRepository;
import com.example.realestate.service.ImageService;
import com.example.realestate.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final PropertyRepository propertyRepository;
    private final ImageRepository imageRepository;
    private final PropertyService propertyService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * Upload multiple images for a property
     */
    @PostMapping("/upload/{propertyId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadImages(
            @PathVariable Long propertyId,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "mainImageIndex", defaultValue = "0") int mainImageIndex) {

        log.info("Uploading {} images for property ID: {}", files.length, propertyId);

        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new RuntimeException("Property not found with ID: " + propertyId));

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            List<ImageDTO> uploadedImages = new ArrayList<>();

            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];

                // Validate file
                if (file.isEmpty()) {
                    log.warn("File {} is empty", i);
                    continue;
                }

                // Generate unique filename
                String originalFilename = file.getOriginalFilename();
                String fileExtension = originalFilename != null ?
                        originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
                String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

                // Save file to server
                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.copy(file.getInputStream(), filePath);

                // Create relative URL for frontend access
                String imageUrl = "/uploads/" + uniqueFilename;

                // Create Image entity
                Image image = Image.builder()
                        .url(imageUrl)
                        .altText(originalFilename)
                        .isMain(i == mainImageIndex)
                        .property(property)
                        .build();

                Image savedImage = imageRepository.save(image);

                // Add to DTO list
                ImageDTO imageDTO = ImageDTO.builder()
                        .id(savedImage.getId())
                        .url(imageUrl)
                        .altText(savedImage.getAltText())
                        .isMain(savedImage.getIsMain())
                        .build();

                uploadedImages.add(imageDTO);

                log.info("Saved image: {} for property ID: {}", imageUrl, propertyId);
            }

            return ResponseEntity.ok(uploadedImages);

        } catch (IOException e) {
            log.error("Error uploading images: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading images: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Get all images for a property
     */
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<ImageDTO>> getPropertyImages(@PathVariable Long propertyId) {
        log.info("Getting images for property ID: {}", propertyId);

        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new RuntimeException("Property not found"));

            List<ImageDTO> images = property.getImages().stream()
                    .map(image -> ImageDTO.builder()
                            .id(image.getId())
                            .url(image.getUrl())
                            .altText(image.getAltText())
                            .isMain(image.getIsMain())
                            .build())
                    .toList();

            return ResponseEntity.ok(images);

        } catch (Exception e) {
            log.error("Error getting images: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Delete an image
     */
    @DeleteMapping("/{imageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        log.info("Deleting image ID: {}", imageId);

        try {
            Image image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new RuntimeException("Image not found"));

            // Delete file from server
            String filename = image.getUrl().replace("/uploads/", "");
            Path filePath = Paths.get(uploadDir, filename);
            Files.deleteIfExists(filePath);

            // Delete from database
            imageRepository.delete(image);

            return ResponseEntity.ok().build();

        } catch (IOException e) {
            log.error("Error deleting file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting file: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting image: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Set image as main
     */
    @PutMapping("/{imageId}/set-main")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> setAsMainImage(@PathVariable Long imageId) {
        log.info("Setting image ID: {} as main", imageId);

        try {
            Image image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new RuntimeException("Image not found"));

            Property property = image.getProperty();

            // Reset all images to non-main
            property.getImages().forEach(img -> img.setIsMain(false));

            // Set this image as main
            image.setIsMain(true);

            imageRepository.saveAll(property.getImages());

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Error setting main image: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}