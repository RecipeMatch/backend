package org.example.recipe_match_backend.domain.recipe.dto.request.recipe;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class RecipeIdAndUserIdRequest {
    private Long recipeId;
    private Long userId;
}
