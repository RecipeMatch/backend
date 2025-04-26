package org.example.recipe_match_backend.domain.recipe.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.example.recipe_match_backend.domain.recipe.domain.QRecipe;
import org.example.recipe_match_backend.domain.recipe.domain.Recipe;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeSearchRequest;
import org.example.recipe_match_backend.domain.user.domain.User;
import org.example.recipe_match_backend.domain.user.domain.UserIngredient;
import org.example.recipe_match_backend.domain.user.domain.UserTool;
import org.example.recipe_match_backend.domain.user.repository.UserRepository;
import org.example.recipe_match_backend.type.AllergyType;
import org.example.recipe_match_backend.type.CategoryType;
import org.example.recipe_match_backend.type.DifficultyType;

import java.util.List;
import java.util.Optional;

public class RecipeRepositoryImpl implements RecipeRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QRecipe recipe;
    private final UserRepository userRepository;

    public RecipeRepositoryImpl(EntityManager em,UserRepository userRepository){
        this.queryFactory = new JPAQueryFactory(em);
        this.recipe = QRecipe.recipe;
        this.userRepository = userRepository;
    }

    @Override
    public List<Recipe> search(RecipeSearchRequest request) {

        Optional<User> optionalUser = request.getUserInfo()
                ? userRepository.findByUid(request.getUserUid())
                : Optional.empty();

        return queryFactory
                .select(recipe)
                .from(recipe)
                .where(
                        keywordLike(request.getKeyword()),
                        difficultyEq(request.getDifficulty()),
                        categoryEq(request.getCategory()),
                        timeBetween(request.getMinTime(), request.getMaxTime()),
                        optionalUser.map(u -> allergiesContainAny(u.getAllergies())).orElse(null),
                        optionalUser.map(u -> userToolsEqAny(u.getUserTools())).orElse(null),
                        optionalUser.map(u -> userIngredientEqAny(u.getUserIngredients())).orElse(null)
                )
                .fetch();
    }

    private BooleanExpression keywordLike(String keyword){
        return keyword != null ? recipe.recipeName.contains(keyword) : null;
    }

    private BooleanExpression difficultyEq(DifficultyType difficulty){
        return difficulty != null ? recipe.difficulty.eq(difficulty) : null;
    }

    private BooleanExpression categoryEq(CategoryType category){
        return category != null ? recipe.category.eq(category) : null;
    }

    private BooleanExpression timeBetween(Integer min, Integer max){
        return (min != null && max != null) ? recipe.cookingTime.between(min,max):null;
    }

    private BooleanExpression allergiesContainAny(List<AllergyType> allergies) {
        if (allergies == null || allergies.isEmpty()) {
            return null;
        }
        BooleanExpression anyAllergies = allergies.stream()
                .map(recipe.allergies::contains)
                .reduce(BooleanExpression::or)
                .orElse(null);
        return anyAllergies.not();
    }

    private BooleanExpression userToolsEqAny(List<UserTool> userTools) {
        if (userTools == null || userTools.isEmpty()) {
            return null;
        }

        return userTools.stream()
                .map(UserTool::getTool)
                .map(tool -> recipe.recipeTools.any().tool.eq(tool))
                .reduce(BooleanExpression::or)
                .orElse(null);
    }

    private BooleanExpression userIngredientEqAny(List<UserIngredient> userIngredients) {
        if (userIngredients == null || userIngredients.isEmpty()) {
            return null;
        }

        return userIngredients.stream()
                .map(UserIngredient::getIngredient)
                .map(ingredient -> recipe.recipeIngredients.any().ingredient.eq(ingredient))
                .reduce(BooleanExpression::or)
                .orElse(null);
    }

}
