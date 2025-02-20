package org.example.recipe_match_backend.domain.recipe.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.ingredient.domain.Ingredient;
import org.example.recipe_match_backend.domain.ingredient.repository.IngredientRepository;
import org.example.recipe_match_backend.domain.recipe.domain.*;
import org.example.recipe_match_backend.domain.recipe.dto.RecipeIngredientDto;
import org.example.recipe_match_backend.domain.recipe.dto.RecipeStepDto;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeRequest;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeUpdateRequest;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeIdAndUserUidResponse;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeAllResponse;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeResponse;
import org.example.recipe_match_backend.domain.recipe.repository.*;
import org.example.recipe_match_backend.domain.tool.domain.Tool;
import org.example.recipe_match_backend.domain.tool.repository.ToolRepository;
import org.example.recipe_match_backend.domain.user.domain.User;
import org.example.recipe_match_backend.domain.user.repository.UserRepository;
import org.example.recipe_match_backend.type.CategoryType;
import org.example.recipe_match_backend.type.DifficultyType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final ToolRepository toolRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final RecipeToolRepository recipeToolRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeBookMarkRepository recipeBookMarkRepository;

    @Transactional
    public RecipeIdAndUserUidResponse save(RecipeRequest request){
        // 사용자 조회
        User user = userRepository.findByUid(request.getUserUid())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Recipe 엔티티 생성
        Recipe recipe = Recipe.builder()
                .recipeName(request.getRecipeName())
                .description(request.getDescription())
                .cookingTime(request.getCookingTime())
                .category(request.getCategory())
                .recipeIngredients(new ArrayList<>())
                .recipeSteps(new ArrayList<>())
                .recipeTools(new ArrayList<>())
                .user(user)
                .build();

        // 사용자와 레시피 관계 설정
        user.addRecipe(recipe);

        // Ingredients 처리
        for (RecipeIngredientDto ingredientDto : request.getRecipeIngredientDtos()) {
            // 기존 Ingredient 조회 또는 새로 생성
            Ingredient ingredient = ingredientRepository.findByIngredientName(ingredientDto.getIngredientName())
                    .orElseGet(() -> {
                        Ingredient newIngredient = Ingredient.builder()
                                .ingredientName(ingredientDto.getIngredientName())
                                .recipeIngredients(new ArrayList<>())
                                .userIngredients(new ArrayList<>())
                                .build();
                        return ingredientRepository.save(newIngredient);
                    });

            // RecipeIngredient 생성
            RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                    .quantity(ingredientDto.getQuantity())
                    .ingredient(ingredient)
                    .build();

            // 양방향 관계 설정
            recipe.addRecipeIngredient(recipeIngredient);
            ingredient.addRecipeIngredient(recipeIngredient);
        }


        // Tools 처리
        for (String toolName : request.getToolName()) {
            Tool tool = toolRepository.findByToolName(toolName)
                    .orElseGet(() -> {
                        Tool newTool = Tool.builder()
                                .toolName(toolName)
                                .recipeTools(new ArrayList<>())
                                .userTools(new ArrayList<>())
                                .build();
                        return toolRepository.save(newTool);
                    });

            // RecipeTool 생성
            RecipeTool recipeTool = RecipeTool.builder()
                    .tool(tool)
                    .build();

            // 양방향 관계 설정
            recipe.addRecipeTool(recipeTool);
            tool.addRecipeTool(recipeTool);
        }

        // RecipeSteps 처리
        for (RecipeStepDto stepDto : request.getRecipeStepDtos()) {
            RecipeStep step = RecipeStep.builder()
                    .stepOrder(stepDto.getStepOrder())
                    .content(stepDto.getContent())
                    .build();
            recipe.addRecipeStep(step);
        }

        recipeDifficulty(recipe, recipe.getCookingTime(), recipe.getRecipeSteps().size(),recipe.getRecipeIngredients().size() , recipe.getRecipeTools().size());

        // Recipe 저장 (CascadeType.PERSIST에 의해 연관된 엔티티들도 함께 저장됨)
        Recipe savedRecipe = recipeRepository.save(recipe);

        return new RecipeIdAndUserUidResponse(request.getUserUid(), recipe.getId());
    }

    @Transactional
    public RecipeIdAndUserUidResponse update(Long recipeId, RecipeUpdateRequest request){

        Recipe recipe = recipeRepository.findById(recipeId).get();

        //null체크
        if(request.getRecipeName() != null){
            recipe.setRecipeName(request.getRecipeName());
        }
        if(request.getCategory() != null){
            recipe.setCategory(request.getCategory());
        }
        if(request.getDescription() != null){
            recipe.setDescription(request.getDescription());
        }
        if(request.getCookingTime() != null){
            recipe.setCookingTime(request.getCookingTime());
        }

        if(request.getToolName() != null){
            //수정된 toolName db에 저장(중복 제외)
            for (String toolName : request.getToolName()) {
                Tool tool = toolRepository.findByToolName(toolName)
                        .orElseGet(() -> {
                            Tool newTool = Tool.builder()
                                    .toolName(toolName)
                                    .recipeTools(new ArrayList<>())
                                    .userTools(new ArrayList<>())
                                    .build();
                            return toolRepository.save(newTool);
                        });

                // RecipeTool 생성
                RecipeTool recipeTool = RecipeTool.builder()
                        .tool(tool)
                        .build();

                // 양방향 관계 설정
                recipe.addRecipeTool(recipeTool);
                tool.addRecipeTool(recipeTool);
            }
        }

        //기존 레시피 도구 객체 삭제
        if(request.getDeleteToolIds() != null){
            for(Long toolId: request.getDeleteToolIds()){
                RecipeTool recipeTool = recipeToolRepository.findById(toolId).get();
                recipeTool.getTool().getRecipeTools().remove(recipeTool);
                recipe.getRecipeTools().remove(recipeTool);
                //recipeToolRepository.deleteById(toolId); : 영속성 전파 ALL
            }
        }

        if(request.getRecipeIngredientDtos() != null){
            //수정된 재료 db에 저장(중복 제외)
            for (RecipeIngredientDto dto : request.getRecipeIngredientDtos()) {
                // 기존 Ingredient 조회 또는 새로 생성
                Ingredient ingredient = ingredientRepository.findByIngredientName(dto.getIngredientName())
                        .orElseGet(() -> {
                            Ingredient newIngredient = Ingredient.builder()
                                    .ingredientName(dto.getIngredientName())
                                    .recipeIngredients(new ArrayList<>())
                                    .userIngredients(new ArrayList<>())
                                    .build();
                            return ingredientRepository.save(newIngredient);
                        });

                // RecipeIngredient 생성
                RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                        .quantity(dto.getQuantity())
                        .ingredient(ingredient)
                        .build();

                // 양방향 관계 설정
                recipe.addRecipeIngredient(recipeIngredient);
                ingredient.addRecipeIngredient(recipeIngredient);
            }
        }

        //기존 레시피 재료 객체 삭제
        if(request.getDeleteIngredientIds() != null){
            for(Long ingredientId: request.getDeleteIngredientIds()){
                RecipeIngredient recipeIngredient = recipeIngredientRepository.findById(ingredientId).get();
                recipeIngredient.getIngredient().getRecipeIngredients().remove(recipeIngredient);
                recipe.getRecipeIngredients().remove(recipeIngredient);
                //recipeIngredientRepository.deleteById(ingredientId); : 영속성 전파 ALL
            }
        }

        if(request.getRecipeStepDtos() != null){
            for (RecipeStepDto stepDto : request.getRecipeStepDtos()) {
                RecipeStep step = RecipeStep.builder()
                        .stepOrder(stepDto.getStepOrder())
                        .content(stepDto.getContent())
                        .build();
                recipe.addRecipeStep(step);
            }
        }

        //기존 레시피 단계 객체 삭제
        if(request.getDeleteStepIds() != null){
            for(Long stepId: request.getDeleteStepIds()){
                RecipeStep recipeStep = recipeStepRepository.findById(stepId).get();
                recipe.getRecipeSteps().remove(recipeStep);
                //recipeStepRepository.deleteById(stepId); : 영속성 전파 ALL
            }
        }

        recipeDifficulty(recipe, recipe.getCookingTime(), recipe.getRecipeSteps().size(),recipe.getRecipeIngredients().size() , recipe.getRecipeTools().size());

        return new RecipeIdAndUserUidResponse(request.getUserUid(), recipeId);
    }

    @Transactional
    public void delete(Long recipeId){
        recipeRepository.deleteById(recipeId);
    }

    public RecipeResponse find(Long recipeId,Long userId){
        Recipe recipe = recipeRepository.findById(recipeId).get();
        User user = userRepository.findById(userId).get();
        Boolean recipeLike = recipeLikeRepository.findByUserAndRecipe(user,recipe).isPresent();
        Boolean recipeBookMark = recipeBookMarkRepository.findByUserAndRecipe(user, recipe).isPresent();
        int likeSize = recipeLikeRepository.findByRecipe(recipe).size();
        int bookMarkSize = recipeBookMarkRepository.findByRecipe(recipe).size();
        return new RecipeResponse(recipe,recipeLike,likeSize,recipeBookMark,bookMarkSize);
    }

    public List<RecipeAllResponse> findAll(){
        List<Recipe> recipes = recipeRepository.findAll();
        return recipes.stream().map(r -> new RecipeAllResponse(r)).collect(toList());
    }

    private void recipeDifficulty(Recipe recipe,int cookingTime, int stepSize, int ingredientSize, int toolSize){

        int time = (cookingTime-5)/175;

        int step = (stepSize - 1)/14;

        int ingredient = (ingredientSize-1)/14;

        int tool = (toolSize-1)/14;

        int point = 25*(time+step+ingredient+tool);

        if(0 <= point && point <= 33){
            recipe.setDifficulty(DifficultyType.초보환영);
        } else if (34 <= point && point <= 66) {
            recipe.setDifficulty(DifficultyType.중간);
        } else if (67 <= point) {
            recipe.setDifficulty(DifficultyType.어려움);
        }
    }
    /**
     * 카테고리별 기본 이미지 경로 반환.
     */
    private String getDefaultImageUrl(CategoryType category) {
        switch (category) {
            case JAPANESE:
                return "/static/images/japanese.jpg";
            case CHINESE:
                return "/static/images/chinese.jpg";
            case KOREAN:
                return "/static/images/korean.jpg";
            case WESTERN:
                return "/static/images/western.jpg";
            default:
                return "/static/images/default.jpg";
        }
    }
}

