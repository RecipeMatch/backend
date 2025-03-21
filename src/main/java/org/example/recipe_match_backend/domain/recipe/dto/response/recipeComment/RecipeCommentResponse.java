package org.example.recipe_match_backend.domain.recipe.dto.response.recipeComment;

import lombok.*;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeComment;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class RecipeCommentResponse {
    private Long id;
    private String nickname;
    private Long recipeId;
    private String content;
}
