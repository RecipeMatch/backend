package org.example.recipe_match_backend.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.recipe_match_backend.domain.recipe.domain.Recipe;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRecipeResponse {
    private Recipe recipe;
}
