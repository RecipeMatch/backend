package org.example.recipe_match_backend.domain.searchhistory.repository;

import org.example.recipe_match_backend.domain.recipe.domain.Recipe;
import org.example.recipe_match_backend.domain.searchhistory.dto.request.RecipeWithScoreDto;
import org.example.recipe_match_backend.domain.searchhistory.dto.request.SearchHistoryRequest;

import java.util.List;

public interface SearchHistoryRepositoryCustom {
    List<RecipeWithScoreDto> recommend(SearchHistoryRequest request);
}
