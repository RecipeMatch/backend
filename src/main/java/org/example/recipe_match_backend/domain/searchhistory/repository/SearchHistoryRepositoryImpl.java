package org.example.recipe_match_backend.domain.searchhistory.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.example.recipe_match_backend.domain.recipe.domain.QRecipe;
import org.example.recipe_match_backend.domain.recipe.domain.Recipe;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeIngredient;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeTool;
import org.example.recipe_match_backend.domain.searchhistory.dto.request.SearchHistoryRequest;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryRepositoryImpl implements SearchHistoryRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QRecipe recipe;

    public SearchHistoryRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
        this.recipe = QRecipe.recipe;
    }

    @Override
    public List<Recipe> recommend(SearchHistoryRequest request) {
        NumberExpression<Integer> scoreExpr =  Expressions.numberTemplate(Integer.class, "0");

        scoreExpr = scoreExpr.add(new CaseBuilder()
                .when(request.getCategoryTypes() != null ? recipe.category.in(request.getCategoryTypes()) : Expressions.FALSE)
                .then(3)
                .otherwise(0));

        scoreExpr = scoreExpr.add(new CaseBuilder()
                .when(request.getDifficultyTypes() != null ? recipe.difficulty.in(request.getDifficultyTypes()):Expressions.FALSE)
                .then(1)
                .otherwise(0));

        scoreExpr = scoreExpr.add(new CaseBuilder()
                .when(request.getRecipeIngredients() != null ? request
                        .getRecipeIngredients()
                        .stream()
                        .map(RecipeIngredient::getIngredient)
                        .map(ingredient -> recipe.recipeIngredients.any().ingredient.eq(ingredient))
                        .reduce(BooleanExpression::or)
                        .orElse(Expressions.FALSE) : Expressions.FALSE)
                .then(2)
                .otherwise(0));

        scoreExpr = scoreExpr.add(new CaseBuilder()
                .when(request.getRecipeTools() != null ? request
                        .getRecipeTools()
                        .stream()
                        .map(RecipeTool::getTool)
                        .map(tool -> recipe.recipeTools.any().tool.eq(tool))
                        .reduce(BooleanExpression::or)
                        .orElse(Expressions.FALSE) : Expressions.FALSE)
                .then(2)
                .otherwise(0));

        return queryFactory
                .select(recipe)
                .from(recipe)
                .orderBy(scoreExpr.desc())
                .limit(10)
                .fetch();
    }

}
