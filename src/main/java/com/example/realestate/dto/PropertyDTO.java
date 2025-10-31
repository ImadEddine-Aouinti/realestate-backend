package com.example.realestate.dto;


import lombok.Data;

@Data
public class PropertyDTO {
    private String title;
    private String description;
    private double price;
    private String type;
    private String imageUrl;
}

