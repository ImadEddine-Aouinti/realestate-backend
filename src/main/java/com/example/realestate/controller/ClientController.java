package com.example.realestate.controller;

import com.example.realestate.entity.Property;
import com.example.realestate.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {
    private final PropertyService propertyService;

    @PostMapping("/addProperty")
    public ResponseEntity<Property> addProperty(@RequestBody Property property) {
        return ResponseEntity.ok(propertyService.addProperty(property));
    }

    @GetMapping("/properties")
    public ResponseEntity<List<Property>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }
}
