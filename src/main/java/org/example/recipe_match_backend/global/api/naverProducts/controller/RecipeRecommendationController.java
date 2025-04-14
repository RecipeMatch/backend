package org.example.recipe_match_backend.global.api.naverProducts.controller;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.global.api.naverProducts.dto.ProductDto;
import org.example.recipe_match_backend.global.api.naverProducts.service.RecipeRecommendationService;
import org.example.recipe_match_backend.global.api.naverProducts.dto.response.ProductRecommendationResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecipeRecommendationController {

    private final RecipeRecommendationService service;

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
}
