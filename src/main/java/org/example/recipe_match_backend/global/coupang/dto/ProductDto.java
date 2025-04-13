package org.example.recipe_match_backend.global.coupang.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class ProductDto {
    private String name;
    private int price;
    private String imageUrl;
    private String productUrl;
}
