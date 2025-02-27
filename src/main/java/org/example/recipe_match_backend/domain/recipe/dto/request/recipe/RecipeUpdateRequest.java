package org.example.recipe_match_backend.domain.recipe.dto.request.recipe;

import lombok.*;
import org.example.recipe_match_backend.domain.recipe.dto.RecipeIngredientDto;
import org.example.recipe_match_backend.domain.recipe.dto.RecipeStepDto;
import org.example.recipe_match_backend.type.CategoryType;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class RecipeUpdateRequest {

    private String userUid;

    private String recipeName; //Recipe

    private String description; //Recipe

    private Integer cookingTime; //Recipe

    private CategoryType category; //Recipe

    private List<MultipartFile> files = new ArrayList<>();

    private List<RecipeIngredientDto> recipeIngredientDtos = new ArrayList<>(); //RecipeIngredient

    private List<RecipeStepDto> recipeStepDtos = new ArrayList<>(); //RecipeStep

    private List<String> toolName = new ArrayList<>(); //RecipeTool

    public void setFiles(List<MultipartFile> files){
        this.files = files;
    }

}
