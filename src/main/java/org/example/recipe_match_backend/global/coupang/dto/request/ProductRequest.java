package org.example.recipe_match_backend.global.coupang.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private int price;
    private String imageUrl;
    private String productUrl;
}
