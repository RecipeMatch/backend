package org.example.recipe_match_backend.global.api.naverProducts.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDto {
    private String title;
    private String link;
    private String image;
    private String lprice;
}
