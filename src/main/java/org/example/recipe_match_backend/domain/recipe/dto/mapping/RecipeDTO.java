package org.example.recipe_match_backend.domain.recipe.dto.mapping;

import lombok.Getter;
import lombok.Setter;
import org.example.recipe_match_backend.type.AllergyType;

import java.util.List;

@Getter
@Setter
public class RecipeDTO {
    private String id;
    private String recipeName;
    private String description;
    private int cookingTime;
    private String difficulty;
    private String category;
    private List<IngredientDTO> ingredients;
    private AllergyType allergy;
    private List<StepDTO> steps;
    private List<String> tools;
    private String alterTools;

}
