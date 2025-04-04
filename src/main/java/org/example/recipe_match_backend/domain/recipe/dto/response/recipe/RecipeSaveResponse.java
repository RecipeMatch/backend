package org.example.recipe_match_backend.domain.recipe.dto.response.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.recipe_match_backend.type.AllergyType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class RecipeSaveResponse {

    private String alterTools;
    private List<AllergyType> allergies = new ArrayList<>();
    private String UserUid;
    private Long RecipeId;
}
