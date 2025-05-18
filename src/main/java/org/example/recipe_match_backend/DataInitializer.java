package org.example.recipe_match_backend;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.recipe.repository.RecipeRepository;
import org.example.recipe_match_backend.domain.recipe.service.RecipeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RecipeService recipeService;
    private final RecipeRepository recipeRepository;
    @Override
    public void run(String... args) throws Exception {
        long count = recipeRepository.count();

        if (count == 0) {
            recipeService.loadRecipesFromJson("src/main/resources/recipe.json");
        } else {
            System.out.println("레시피가 이미 DB에 존재하므로 JSON 로딩을 건너뜁니다.");
        }
    }
}
