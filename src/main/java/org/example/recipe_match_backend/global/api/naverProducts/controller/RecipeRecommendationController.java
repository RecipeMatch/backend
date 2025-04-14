package org.example.recipe_match_backend.global.api.naverProducts.controller;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.global.api.naverProducts.dto.ProductDto;
import org.example.recipe_match_backend.global.api.naverProducts.dto.response.SearchProductRecommendationResponse;
import org.example.recipe_match_backend.global.api.naverProducts.service.RecipeRecommendationService;
import org.example.recipe_match_backend.global.api.naverProducts.dto.response.ProductRecommendationResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecipeRecommendationController {

    private final RecipeRecommendationService service;

    // 부족한 재료 관련 상품 5가지 반환
    @GetMapping("/recommendations/products")
    public ProductRecommendationResponse recommendProducts(
            @RequestParam Long recipeId,
            @RequestParam String userUid){

        List<ProductDto> products = service.recommendProducts(recipeId, userUid);

        return ProductRecommendationResponse.builder()
                .hasMissing(!products.isEmpty())
                .products(products)
                .build();
    }

    // 검색한 키워드 관련 상품 5가지 반환
    @GetMapping("/search/recommendations/products")
    public SearchProductRecommendationResponse searchRecommendProducts(
            @RequestParam String keyword){

        List<ProductDto> products = service.searchRecommendProducts(keyword);

        return SearchProductRecommendationResponse.builder()
                .products(products)
                .build();
    }
}
