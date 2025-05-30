package org.example.recipe_match_backend.domain.searchhistory.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.recipe_match_backend.domain.recipe.domain.Recipe;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class RecipeWithScoreDto {
    private Recipe recipe;
    private Double scoreExpr;
}
