package org.example.recipe_match_backend.domain.searchhistory.service;

import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.recipe.domain.Recipe;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeImage;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeIngredient;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeTool;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeRequest;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeResponse;
import org.example.recipe_match_backend.domain.recipe.repository.RecipeBookMarkRepository;
import org.example.recipe_match_backend.domain.recipe.repository.RecipeLikeRepository;
import org.example.recipe_match_backend.domain.searchhistory.domain.SearchHistory;
import org.example.recipe_match_backend.domain.searchhistory.dto.request.SearchHistoryRequest;
import org.example.recipe_match_backend.domain.searchhistory.repository.SearchHistoryRepository;
import org.example.recipe_match_backend.domain.user.domain.User;
import org.example.recipe_match_backend.domain.user.repository.UserRepository;
import org.example.recipe_match_backend.type.CategoryType;
import org.example.recipe_match_backend.type.DifficultyType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeBookMarkRepository recipeBookMarkRepository;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public List<RecipeResponse> recommended_Recipe(String uid){

        User user = userRepository.findByUid(uid).orElseThrow();
        List<SearchHistory> searchHistories = user.getSearchHistories();
        List<Recipe> recipes = searchHistories.stream().map(SearchHistory::getRecipe).toList();

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        List<RecipeTool> recipeTools = new ArrayList<>();
        List<CategoryType> categoryTypes = searchHistories.stream().map(SearchHistory::getCategoryType).toList();
        List<DifficultyType> difficultyTypes = new ArrayList<>();
        for(Recipe recipe:recipes){
            recipeIngredients.addAll(recipe.getRecipeIngredients());
            recipeTools.addAll(recipe.getRecipeTools());
            difficultyTypes.add(recipe.getDifficulty());
        }

        SearchHistoryRequest request = new SearchHistoryRequest(recipeIngredients,recipeTools,categoryTypes,difficultyTypes);

        List<Recipe> recommendRecipes = searchHistoryRepository.recommend(request);

        List<RecipeResponse> recipeResponses = new ArrayList<>();

        for(Recipe recipe:recommendRecipes){//이제 여기서 recipes를 추천하는 레시피 리스트로 바꿔야함: querydsl을 통해 점수 기반 추천
            int likeSize = recipeLikeRepository.findByRecipe(recipe).size();
            int bookMarkSize = recipeBookMarkRepository.findByRecipe(recipe).size();

            List<String> urls = new ArrayList<>();
            for(RecipeImage recipeImage:recipe.getRecipeImages()){
                urls.add(""+amazonS3Client.getUrl(bucketName, recipeImage.getToken()));
            }
            recipeResponses.add(new RecipeResponse(recipe,likeSize,bookMarkSize,urls));
        }
        return recipeResponses;
    }

}
