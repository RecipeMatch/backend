package org.example.recipe_match_backend.domain.recipe.dto.request.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.recipe_match_backend.type.CategoryType;
import org.example.recipe_match_backend.type.DifficultyType;
import org.example.recipe_match_backend.type.RecommendType;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class RecipeSearchRequest {
    private String userUid;
    private String keyword;
    private DifficultyType difficulty;
    private CategoryType category;
    private int cookingTime;
    private Boolean userInfo;
}
