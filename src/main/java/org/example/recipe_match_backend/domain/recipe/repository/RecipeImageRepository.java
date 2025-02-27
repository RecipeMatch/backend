package org.example.recipe_match_backend.domain.recipe.repository;

import org.example.recipe_match_backend.domain.recipe.domain.RecipeImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeImageRepository extends JpaRepository<RecipeImage,Long> {
}
