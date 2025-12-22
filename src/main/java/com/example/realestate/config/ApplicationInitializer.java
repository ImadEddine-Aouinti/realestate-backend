package com.example.realestate.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class ApplicationInitializer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("✅ Upload directory created: " + uploadPath.toAbsolutePath());
            } else {
                System.out.println("📁 Upload directory already exists: " + uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to create upload directory: " + e.getMessage());
        }
    }
}