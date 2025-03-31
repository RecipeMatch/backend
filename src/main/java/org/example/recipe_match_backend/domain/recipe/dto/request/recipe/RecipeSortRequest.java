package org.example.recipe_match_backend.domain.recipe.dto.request.recipe;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class RecipeSortRequest {
    private List<Long> recipeIds;
    private String sortBy;          // "like" 또는 "bookmark"
}
