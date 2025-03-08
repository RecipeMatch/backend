package org.example.recipe_match_backend.domain.recipe.repository;

import org.example.recipe_match_backend.domain.recipe.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe,Long> {

    @Query("select r from Recipe r where r.recipeName like concat('%', :keyword, '%')")
    List<Recipe> findByKeyword(@Param("keyword") String keyword);
}
