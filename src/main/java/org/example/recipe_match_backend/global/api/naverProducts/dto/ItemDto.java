package org.example.recipe_match_backend.global.api.naverProducts.dto;

import lombok.Data;

@Data
public class ItemDto {
    private String title;
    private String link;
    private String image;
    private String lprice;
}
