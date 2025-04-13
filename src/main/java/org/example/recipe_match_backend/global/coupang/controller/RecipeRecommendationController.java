package org.example.recipe_match_backend.global.coupang.controller;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.global.coupang.dto.ProductDto;
import org.example.recipe_match_backend.global.coupang.dto.response.ProductRecommendationResponse;
import org.example.recipe_match_backend.global.coupang.service.RecipeRecommendationService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecipeRecommendationController {

    private final RecipeRecommendationService service;

    public ProductRecommendationResponse recommendProducts(
            @PathVariable Long recipeId,
            @RequestParam String userUid){

        List<ProductDto> products = service.recommendProducts(recipeId, userUid);

        return ProductRecommendationResponse.builder()
                .hasMissing(!products.isEmpty())
                .products(products)
                .build();
    }

}
