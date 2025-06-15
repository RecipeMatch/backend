package org.example.recipe_match_backend.domain.searchhistory.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.recipe_match_backend.domain.recipe.domain.Recipe;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeIngredient;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeTool;
import org.example.recipe_match_backend.domain.searchhistory.domain.SearchHistory;
import org.example.recipe_match_backend.type.CategoryType;
import org.example.recipe_match_backend.type.DifficultyType;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class SearchHistoryRequest {

    private String uid;
    private Boolean userInfo;
    private List<Recipe> recipes = new ArrayList<>();
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();
    private List<RecipeTool> recipeTools = new ArrayList<>();
    private List<CategoryType> categoryTypes = new ArrayList<>();
    private List<DifficultyType> difficultyTypes = new ArrayList<>();

}
