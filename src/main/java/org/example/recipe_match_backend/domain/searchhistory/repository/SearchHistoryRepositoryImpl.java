package org.example.recipe_match_backend.domain.searchhistory.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.example.recipe_match_backend.domain.recipe.domain.QRecipe;
import org.example.recipe_match_backend.domain.recipe.domain.Recipe;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeIngredient;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeTool;
import org.example.recipe_match_backend.domain.searchhistory.dto.request.SearchHistoryRequest;
import org.example.recipe_match_backend.domain.user.domain.User;
import org.example.recipe_match_backend.domain.user.repository.UserRepository;
import org.example.recipe_match_backend.type.AllergyType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.example.recipe_match_backend.domain.recipe.domain.QRecipeIngredient.recipeIngredient;
import static org.example.recipe_match_backend.domain.recipe.domain.QRecipeTool.recipeTool;

@Slf4j
public class SearchHistoryRepositoryImpl implements SearchHistoryRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QRecipe recipe;
    private final UserRepository userRepository;

    public SearchHistoryRepositoryImpl(EntityManager em, UserRepository userRepository){
        this.queryFactory = new JPAQueryFactory(em);
        this.recipe = QRecipe.recipe;
        this.userRepository = userRepository;
    }

    @Override
    public List<Recipe> recommend(SearchHistoryRequest request) {

        NumberExpression<Integer> scoreExpr =  buildScoreExpr(request);

        Optional<User> optionalUser = userRepository.findByUid(request.getUid());

        return queryFactory
                .select(recipe)
                .from(recipe)
                .where(
                        optionalUser.map(u -> allergiesContainAny(u.getAllergies())).orElse(null),
                        optionalUser.map(u -> duplicateAny(request.getRecipes())).orElse(null)
                )
                .orderBy(scoreExpr.desc())
                .limit(5)
                .fetch();

        /**List<Recipe> recipes= queryFactory
                .select(recipe)
                .from(recipe)
                .where(optionalUser.map(u -> allergiesContainAny(u.getAllergies())).orElse(null))
                .orderBy(scoreExpr.desc())
                .fetch();

        return recipes.stream()
                .limit(5)
                .toList();**/
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

    private BooleanExpression duplicateAny(List<Recipe> recipes){
        if(recipes == null || recipes.isEmpty()){
            return null;
        }
        return recipe.notIn(recipes);
    }

    private  NumberExpression<Integer> buildScoreExpr(SearchHistoryRequest request){
        NumberExpression<Integer> scoreExpr =  Expressions.numberTemplate(Integer.class, "0");

        scoreExpr = scoreExpr.add(new CaseBuilder()
                .when(request.getCategoryTypes() != null ? recipe.category.in(request.getCategoryTypes()) : Expressions.FALSE)
                .then(4)
                .otherwise(0));

        scoreExpr = scoreExpr.add(new CaseBuilder()
                .when(request.getDifficultyTypes() != null ? recipe.difficulty.in(request.getDifficultyTypes()):Expressions.FALSE)
                .then(2)
                .otherwise(0));

        for (RecipeIngredient recipeIngredient : request.getRecipeIngredients()) {
            scoreExpr = scoreExpr.add(
                    new CaseBuilder()
                            .when(recipe.recipeIngredients.any().ingredient.eq(recipeIngredient.getIngredient()))
                            .then(2)
                            .otherwise(0)
            );
        }

        for(RecipeTool recipeTool: request.getRecipeTools()){
            scoreExpr = scoreExpr.add(
                    new CaseBuilder()
                            .when(recipe.recipeTools.any().tool.eq(recipeTool.getTool()))
                            .then(1)
                            .otherwise(0)
            );
        }

        return scoreExpr;
    }

}
