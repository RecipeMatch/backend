package org.example.recipe_match_backend.domain.recipe.dto.response.recipe;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.example.recipe_match_backend.domain.recipe.domain.Recipe;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeLike;
import org.example.recipe_match_backend.domain.recipe.dto.RecipeIngredientDto;
import org.example.recipe_match_backend.domain.recipe.dto.RecipeStepDto;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeTool;
import org.example.recipe_match_backend.type.CategoryType;
import org.example.recipe_match_backend.type.DifficultyType;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class RecipeResponse {

    private Long id;

    private String recipeName;//Recipe

    private String description;//Recipe

    private Integer cookingTime;//Recipe

    private DifficultyType difficulty;//Recipe

    private CategoryType category;//Recipe

    private List<RecipeIngredientDto> recipeIngredientDtos = new ArrayList<>();//RecipeIngredient

    private List<RecipeStepDto> recipeStepDtos = new ArrayList<>();//RecipeStep

    private List<String> toolName = new ArrayList<>();//RecipeTool

    private Boolean recipeLike;

    private int likeSize;

    private Boolean recipeBookMark;

    private int bookMarkSize;

    public RecipeResponse(Recipe recipe, Boolean recipeLike, int likeSize,Boolean recipeBookMark, int bookMarkSize){
        this.id = recipe.getId();
        this.recipeName = recipe.getRecipeName();
        this.description = recipe.getDescription();
        this.cookingTime = recipe.getCookingTime();
        this.difficulty = recipe.getDifficulty();
        this.category = recipe.getCategory();
        this.recipeIngredientDtos = recipe.getRecipeIngredients().stream().map(r->new RecipeIngredientDto(r)).collect(toList());
        this.recipeStepDtos = recipe.getRecipeSteps().stream().map(s -> new RecipeStepDto(s)).collect(toList());
        for(RecipeTool recipeTool:recipe.getRecipeTools()){
            this.toolName.add(recipeTool.getTool().getToolName());
        }
        this.recipeLike = recipeLike;
        this.likeSize = likeSize;
        this.recipeBookMark = recipeBookMark;
        this.bookMarkSize = bookMarkSize;
    }
}
