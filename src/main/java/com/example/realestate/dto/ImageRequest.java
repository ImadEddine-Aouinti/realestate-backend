package com.example.realestate.dto;

import lombok.Data;

@Data
public class ImageRequest {
    private String url;
    private String altText;
    private Boolean isMain = false;
}