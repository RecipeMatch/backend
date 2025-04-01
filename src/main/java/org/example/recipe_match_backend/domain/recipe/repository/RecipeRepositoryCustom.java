package org.example.recipe_match_backend.domain.recipe.repository;

import org.example.recipe_match_backend.domain.recipe.domain.Recipe;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeSearchRequest;

import java.util.List;

public interface RecipeRepositoryCustom {

    List<Recipe> search(RecipeSearchRequest request);

}
