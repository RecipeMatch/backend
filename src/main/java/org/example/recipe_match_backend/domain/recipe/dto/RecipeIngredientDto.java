package org.example.recipe_match_backend.domain.recipe.dto;

import lombok.*;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeIngredient;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class RecipeIngredientDto {

    private Long id;
    private String quantity;
    private String ingredientName;

    public RecipeIngredientDto(RecipeIngredient recipeIngredient){
        this.id = recipeIngredient.getId();
        this.quantity = recipeIngredient.getQuantity();
        this.ingredientName = recipeIngredient.getIngredient().getIngredientName();
    }

}
