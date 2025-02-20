package org.example.recipe_match_backend.domain.recipe.dto.request.recipe;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.example.recipe_match_backend.domain.recipe.domain.Recipe;
import org.example.recipe_match_backend.domain.recipe.dto.RecipeIngredientDto;
import org.example.recipe_match_backend.domain.recipe.dto.RecipeStepDto;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeTool;
import org.example.recipe_match_backend.type.CategoryType;
import org.example.recipe_match_backend.type.DifficultyType;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class RecipeRequest {

    private String userUid;

    private String recipeName;      //Recipe

    private String description;     //Recipe

    private Integer cookingTime;    //Recipe

    private CategoryType category;  //Recipe

    private List<RecipeIngredientDto> recipeIngredientDtos = new ArrayList<>();//RecipeIngredient

    private List<RecipeStepDto> recipeStepDtos = new ArrayList<>();//RecipeStep

    private List<String> toolName = new ArrayList<>();//RecipeTool
}
