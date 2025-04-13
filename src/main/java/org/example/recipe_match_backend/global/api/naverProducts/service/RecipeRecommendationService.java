package org.example.recipe_match_backend.global.api.naverProducts.service;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.recipe.domain.Recipe;
import org.example.recipe_match_backend.domain.recipe.repository.RecipeRepository;
import org.example.recipe_match_backend.domain.user.domain.User;
import org.example.recipe_match_backend.domain.user.repository.UserRepository;
import org.example.recipe_match_backend.global.api.naverProducts.dto.ProductDto;
import org.example.recipe_match_backend.global.config.NaverShoppingClient;
import org.example.recipe_match_backend.global.exception.recipe.RecipeNotFoundException;
import org.example.recipe_match_backend.global.exception.user.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeRecommendationService {

    private final NaverShoppingClient client;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public List<ProductDto> recommendProducts(Long recipeId, String userUid) {

        User user = userRepository.findByUid(userUid).orElseThrow(UserNotFoundException::new);
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(RecipeNotFoundException::new);

        List<String> recipeIngredients = recipe.getRecipeIngredients()
                .stream()
                .map(ri -> ri.getIngredient().getIngredientName())
                .toList();

        List<String> userIngredients = user.getUserIngredients()
                .stream()
                .map(ui -> ui.getIngredient().getIngredientName())
                .toList();

        List<String> missing = new ArrayList<>(recipeIngredients);
        missing.removeAll(userIngredients);

        if(missing.isEmpty()) return List.of();

        String keyword = missing.get(ThreadLocalRandom.current().nextInt(missing.size()));

        return client.searchProducts(keyword, 5);
    }
}
