package com.example.realestate.service;

import com.example.realestate.entity.Property;
import com.example.realestate.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {
    private final PropertyRepository propertyRepository;

    public Property addProperty(Property property) {
        return propertyRepository.save(property);
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }
}

