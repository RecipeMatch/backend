package org.example.recipe_match_backend.domain.recipe.service;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.recipe.domain.Recipe;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeLike;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeIdAndUserIdRequest;
import org.example.recipe_match_backend.domain.recipe.repository.RecipeLikeRepository;
import org.example.recipe_match_backend.domain.recipe.repository.RecipeRepository;
import org.example.recipe_match_backend.domain.user.domain.User;
import org.example.recipe_match_backend.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeLikeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RecipeLikeRepository recipeLikeRepository;

    @Transactional
    public Long recipeLike(RecipeIdAndUserIdRequest request){
        Recipe recipe = recipeRepository.findById(request.getRecipeId()).get();
        User user = userRepository.findById(request.getUserId()).get();
        if (recipeLikeRepository.findByUserAndRecipe(user, recipe).isEmpty()){
            RecipeLike recipeLike = RecipeLike.builder().recipe(recipe).user(user).build();
            recipe.getRecipeLikes().add(recipeLike);
            user.getRecipeLikes().add(recipeLike);
            return recipeLike.getId();
        }
        else{
            recipeLikeRepository.deleteByUserAndRecipe(user,recipe);
            return null;
        }
    }
}
