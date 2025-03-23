package org.example.recipe_match_backend.domain.recipe.dto.mapping;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientDTO {
    private String ingredientName;
    private String quantity;
}
