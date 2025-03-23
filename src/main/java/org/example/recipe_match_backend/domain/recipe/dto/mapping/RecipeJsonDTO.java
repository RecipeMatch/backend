package org.example.recipe_match_backend.domain.recipe.dto.mapping;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecipeJsonDTO {
    private String id;
    private String recipeName;
    private String description;
    private int cookingTime;
    private String difficulty;
    private String category;
    private List<IngredientJsonDTO> ingredients;
    private String allergy;
    private List<RecipeStepJsonDTO> steps;
    private List<String> tools;
    private String alterTools;

}
