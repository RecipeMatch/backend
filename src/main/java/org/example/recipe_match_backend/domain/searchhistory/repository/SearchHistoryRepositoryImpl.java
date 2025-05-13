package org.example.recipe_match_backend.domain.searchhistory.repository;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.example.recipe_match_backend.domain.ingredient.domain.Ingredient;
import org.example.recipe_match_backend.domain.recipe.domain.QRecipe;
import org.example.recipe_match_backend.domain.recipe.domain.Recipe;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeIngredient;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeTool;
import org.example.recipe_match_backend.domain.searchhistory.dto.request.SearchHistoryRequest;
import org.example.recipe_match_backend.domain.tool.domain.Tool;
import org.example.recipe_match_backend.domain.user.domain.User;
import org.example.recipe_match_backend.domain.user.repository.UserRepository;
import org.example.recipe_match_backend.type.AllergyType;

import java.sql.Array;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        NumberExpression<Double> scoreExpr =  buildScoreExpr(request);

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

    private  NumberExpression<Double> buildScoreExpr(SearchHistoryRequest request){
        NumberExpression<Double> scoreExpr =  Expressions.numberTemplate(Double.class, "0");

        scoreExpr = scoreExpr.add(new CaseBuilder()
                .when(request.getCategoryTypes() != null ? recipe.category.in(request.getCategoryTypes()) : Expressions.FALSE)
                .then(3.0)
                .otherwise(0.0));

        scoreExpr = scoreExpr.add(new CaseBuilder()
                .when(request.getDifficultyTypes() != null ? recipe.difficulty.in(request.getDifficultyTypes()):Expressions.FALSE)
                .then(1.0)
                .otherwise(0.0));

        NumberExpression<Double> jaccardScore = similarity(request);

        scoreExpr = scoreExpr.add(jaccardScore);

        return scoreExpr;
    }

    private NumberExpression<Double> similarity(SearchHistoryRequest request) {

        NumberExpression<Double> score =  Expressions.numberTemplate(Double.class, "0");

        //레시피 재료에 대한 자카드 유사도
        Set<String> commonIngredientsToExclude = Set.of(
                "소금", "물", "후춧가루", "간장", "설탕", "참기름","고춧가루","국간장","식용유",
                "다진마늘","대파","물엿","깨소금","다진파","생강즙","고추장","된장",
                "마늘","초고추장","양념간장","청주","식초","배즙","양파즙","양념장","녹말","무명실",
                "파슬리가루","생강","통깨","깨","월계수잎","실파","다진생강","진간장",
                "겨자", "겨자잎", "계핏가루", "다진양파", "들기름", "레몬즙","마늘종",
                "버터", "슈가파우더", "쌈장", "연겨자", "올리브오일", "전분",
                "통후추", "파마산치즈", "파슬리", "황설탕", "후추", "흑설탕", "흰설탕"
        );

        List<Ingredient> userIngredients = request
                .getRecipeIngredients()
                .stream()
                .map(RecipeIngredient::getIngredient)
                .filter(ingredient -> !commonIngredientsToExclude.contains(ingredient.getIngredientName()))
                .toList();

        List<String> MainIngredients = new ArrayList<>(Arrays.asList(
                "돼지고기","쇠고기","닭","닭고기","가리비", "가재새우","검은껍질홍합", "고등어",
                "꼴뚜기", "꽁치", "꽃게", "낙지","문어", "바지락",
                "북어", "새우", "생새우", "생태", "연어", "오징어",
                "재첩", "조개살", "조기", "중새우살", "쭈꾸미", "홍합", "훈제연어",
                "감자", "배추", "송이버섯", "오이", "호박","청포묵","팥","두부"
        ));

        //NumberExpression<Double> IngredientCount = recipe.recipeIngredients.size().doubleValue();
        //NumberExpression<Double> recipeIngredientCount = Expressions.numberTemplate(Double.class, "{0}", IngredientCount);
        NumberExpression<Double> recipeIngredientCount =recipe.recipeIngredients.size().doubleValue();

        score = score.add(similarityCalculate(
                userIngredients,
                MainIngredients,
                recipeIngredientCount,
                ing -> recipe.recipeIngredients.any().ingredient.eq(ing),
                (ing, main)  -> recipe.recipeIngredients.any().ingredient.eq(ing).and(recipe.recipeIngredients.any().ingredient.ingredientName.eq(main)),
                30.0));

        List<Tool> userTools = request
                .getRecipeTools()
                .stream()
                .map(RecipeTool::getTool)
                .toList();

        List<String> MainTools = new ArrayList<>(Arrays.asList(
                "그릴", "김밥매트", "냄비", "돌솥", "뚝배기", "발효통", "밥솥", "석쇠", "솥", "스팀기","압력밥솥", "압력솥",
                "오븐", "원형 틀", "유부틀", "전골냄비", "찜통", "튀김용 냄비", "튀김팬", "팬", "프라이팬"
        ));

        //NumberExpression<Double> ToolCount = recipe.recipeTools.size().doubleValue();
        //NumberExpression<Double> recipeToolCount = Expressions.numberTemplate(Double.class, "{0}", ToolCount);
        NumberExpression<Double> recipeToolCount = recipe.recipeTools.size().doubleValue();;

        score = score.add(similarityCalculate(
                userTools,
                MainTools,
                recipeToolCount,
                tool -> recipe.recipeTools.any().tool.eq(tool),
                (tool,main) -> recipe.recipeTools.any().tool.eq(tool).and(recipe.recipeTools.any().tool.toolName.eq(main)),
                10.0));

        return score;

    }

    private <T,F> NumberExpression<Double> similarityCalculate(List<T> userValues,// 사용자 입력 값 (Ingredient, Tool 등)
                                                               List<F> userMain,
                                                               Expression<Double> recipeSizeExpr,// recipe.recipeIngredients / recipeTools 개수
                                                               Function<T, BooleanExpression> matchCondition,// any().ingredient.eq(x) 또는 any().tool.eq(x)
                                                               BiFunction<T, F, BooleanExpression> matchMain,
                                                               double weight){

        NumberExpression<Double> count =  Expressions.numberTemplate(Double.class, "0");
        NumberExpression<Double> matchCount =  Expressions.numberTemplate(Double.class, "0");
        NumberExpression<Double> unionCount = Expressions.numberTemplate(Double.class, String.valueOf(userValues.size()));

        List<NumberExpression<Double>> expressions = new ArrayList<>();

        for(T userValue: userValues){
            for (F main: userMain) {
                count = count.add(
                        new CaseBuilder()
                                .when(matchMain.apply(userValue,main))
                                .then(2.0)
                                .otherwise(0.0)
                );
            }
            matchCount = matchCount.add(
                    new CaseBuilder()
                            .when(matchCondition.apply(userValue))
                            .then(1.0)
                            .otherwise(0.0)
            );
            count = count.add(
                    new CaseBuilder()
                            .when(matchCondition.apply(userValue))
                            .then(1.0)
                            .otherwise(0.0)
            );
        }

        unionCount = unionCount.add(recipeSizeExpr).subtract(matchCount);

        return Expressions.numberTemplate(
                Double.class,
                "({0} * {1}) / nullif({2}, 0)",
                count, Expressions.constant(weight), unionCount
        );
    }

}
