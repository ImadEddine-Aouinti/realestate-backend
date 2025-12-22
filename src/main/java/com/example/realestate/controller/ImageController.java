package com.example.realestate.controller;

import com.example.realestate.dto.ImageDTO;
import com.example.realestate.entity.Image;
import com.example.realestate.entity.Property;
import com.example.realestate.repository.ImageRepository;
import com.example.realestate.repository.PropertyRepository;
import com.example.realestate.service.ImageService;
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

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * Upload multiple images for a property
     */
    @PostMapping(value = "/upload/{propertyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadImages(
            @PathVariable Long propertyId,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "mainImageIndex", defaultValue = "0") int mainImageIndex) {

        log.info("📤 Upload de {} images pour propriété ID: {}", files.length, propertyId);

        try {
            // Trouver la propriété
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new RuntimeException("Propriété non trouvée avec ID: " + propertyId));

            log.info("✅ Propriété trouvée: {}", property.getTitle());

            // Créer le dossier uploads s'il n'existe pas
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("📁 Dossier uploads créé: {}", uploadPath.toAbsolutePath());
            }

            List<ImageDTO> uploadedImages = new ArrayList<>();

            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];

                log.info("📄 Traitement fichier {}: {}, taille: {} bytes",
                        i, file.getOriginalFilename(), file.getSize());

                if (file.isEmpty()) {
                    log.warn("⚠️ Fichier {} est vide", i);
                    continue;
                }

                // Générer un nom de fichier unique
                String originalFilename = file.getOriginalFilename();
                String fileExtension = originalFilename != null ?
                        originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
                String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

                // Sauvegarder le fichier
                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.copy(file.getInputStream(), filePath);

                // Créer l'URL d'accès
                String imageUrl = "/uploads/" + uniqueFilename;

                log.info("💾 Fichier sauvegardé: {}", filePath.toAbsolutePath());
                log.info("🔗 URL image: {}", imageUrl);

                // Créer l'entité Image
                Image image = Image.builder()
                        .url(imageUrl)
                        .altText(originalFilename != null ? originalFilename : "Image " + i)
                        .isMain(i == mainImageIndex)
                        .property(property)
                        .build();

                Image savedImage = imageRepository.save(image);

                log.info("💾 Image enregistrée en base avec ID: {}", savedImage.getId());

                // Créer le DTO de réponse
                ImageDTO imageDTO = ImageDTO.builder()
                        .id(savedImage.getId())
                        .url(imageUrl)
                        .altText(savedImage.getAltText())
                        .isMain(savedImage.getIsMain())
                        .build();

                uploadedImages.add(imageDTO);
            }

            log.info("✅ {} images uploadées avec succès pour propriété ID: {}",
                    uploadedImages.size(), propertyId);

            return ResponseEntity.ok(uploadedImages);

        } catch (IOException e) {
            log.error("❌ Erreur IO lors de l'upload: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'upload des images: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Erreur inattendue: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("Erreur: " + e.getMessage());
        }
    }

    /**
     * Get all images for a property
     */
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<ImageDTO>> getPropertyImages(@PathVariable Long propertyId) {
        log.info("📥 Récupération images pour propriété ID: {}", propertyId);

        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new RuntimeException("Propriété non trouvée"));

            List<ImageDTO> images = property.getImages().stream()
                    .map(image -> ImageDTO.builder()
                            .id(image.getId())
                            .url(image.getUrl())
                            .altText(image.getAltText())
                            .isMain(image.getIsMain())
                            .build())
                    .toList();

            log.info("✅ {} images trouvées pour propriété ID: {}", images.size(), propertyId);
            return ResponseEntity.ok(images);

        } catch (Exception e) {
            log.error("❌ Erreur récupération images: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Delete an image
     */
    @DeleteMapping("/{imageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        log.info("🗑️ Suppression image ID: {}", imageId);

        try {
            Image image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new RuntimeException("Image non trouvée"));

            // Supprimer le fichier du serveur
            String filename = image.getUrl().replace("/uploads/", "");
            Path filePath = Paths.get(uploadDir, filename);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("🗑️ Fichier supprimé: {}", filePath.toAbsolutePath());
            }

            // Supprimer de la base de données
            imageRepository.delete(image);

            log.info("✅ Image ID: {} supprimée avec succès", imageId);
            return ResponseEntity.ok().build();

        } catch (IOException e) {
            log.error("❌ Erreur suppression fichier: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur suppression fichier: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Erreur suppression image: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    /**
     * Set image as main
     */
    @PutMapping("/{imageId}/set-main")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> setAsMainImage(@PathVariable Long imageId) {
        log.info("⭐ Définition image ID: {} comme principale", imageId);

        try {
            Image image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new RuntimeException("Image non trouvée"));

            Property property = image.getProperty();

            // Réinitialiser toutes les images
            for (Image img : property.getImages()) {
                img.setIsMain(false);
            }

            // Définir cette image comme principale
            image.setIsMain(true);

            imageRepository.saveAll(property.getImages());

            log.info("✅ Image ID: {} définie comme principale", imageId);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("❌ Erreur définition image principale: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
}