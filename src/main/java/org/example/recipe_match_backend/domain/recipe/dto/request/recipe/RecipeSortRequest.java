package org.example.recipe_match_backend.domain.recipe.dto.request.recipe;

import lombok.Data;
import org.example.recipe_match_backend.type.RecommendType;

import java.util.List;

@Data
public class RecipeSortRequest {
    private List<Long> recipeIds;
    private RecommendType sortBy;          // "like" 또는 "bookmark"
}
