package org.example.recipe_match_backend.domain.recipe.service;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.recipe.domain.Recipe;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeBookMark;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeLike;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeIdAndUserIdRequest;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeAllResponse;
import org.example.recipe_match_backend.domain.recipe.repository.RecipeBookMarkRepository;
import org.example.recipe_match_backend.domain.recipe.repository.RecipeRepository;
import org.example.recipe_match_backend.domain.user.domain.User;
import org.example.recipe_match_backend.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeBookMarkService {

    private final RecipeBookMarkRepository recipeBookMarkRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long recipeBookMark(RecipeIdAndUserIdRequest request){

        Recipe recipe = recipeRepository.findById(request.getRecipeId()).get();
        User user = userRepository.findById(request.getUserId()).get();
        if (recipeBookMarkRepository.findByUserAndRecipe(user, recipe).isEmpty()){
            RecipeBookMark recipeBookMark = RecipeBookMark.builder().recipe(recipe).user(user).build();
            recipe.getRecipeFavorites().add(recipeBookMark);
            user.getRecipeFavorites().add(recipeBookMark);
            return recipeBookMark.getId();
        }
        else{
            recipeBookMarkRepository.deleteByUserAndRecipe(user,recipe);
            return null;
        }
    }

    public List<RecipeAllResponse> findBookMark(Long userId){
        User user = userRepository.findById(userId).get();
        List<RecipeBookMark> recipeBookMarks = recipeBookMarkRepository.findByUser(user);
        return recipeBookMarks.stream().map(r -> new RecipeAllResponse(r.getRecipe())).collect(toList());
    }

}
