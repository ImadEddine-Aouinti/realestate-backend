// ImageDTO.java
package com.example.realestate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageDTO {
    private Long id;
    private String url;
    private String altText;
    private Boolean isMain;
}