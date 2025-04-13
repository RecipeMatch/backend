package org.example.recipe_match_backend.global.api.naverProducts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class ProductDto {
    private String name;            // 상품 이름
    private int price;              // 상품 가격
    private String imageUrl;        // 상품 이미지 url
    private String productUrl;      // 상품 정보 url

}
