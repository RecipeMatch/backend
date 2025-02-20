package org.example.recipe_match_backend.domain.recipe.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.recipe_match_backend.global.entity.BaseEntity;
import org.example.recipe_match_backend.domain.searchhistory.domain.SearchHistory;
import org.example.recipe_match_backend.type.CategoryType;
import org.example.recipe_match_backend.type.DifficultyType;
import org.example.recipe_match_backend.domain.user.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
@Entity
public class Recipe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //이름:레시피 입력시 필요한 내용
    @Column(nullable = false, unique = true)
    private String recipeName;

    //설명:레시피 입력시 필요한 내용
    @Column(length = 2000)
    private String description;

    //시간:레시피 입력시 필요한 내용
    private int cookingTime;

    //난이도:레시피 입력시 필요한 내용
    @Enumerated(EnumType.STRING)
    private DifficultyType difficulty;

    //카테고리:레시피 입력시 필요한 내용
    @Enumerated(EnumType.STRING)
    private CategoryType category;

    //사용자:레시피 입력시 필요한 내용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //레시피 단계 및 내용:레시피 입력시 필요한 내용
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeStep> recipeSteps = new ArrayList<>();

    //재료:레시피 입력시 필요한 내용
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    //도구:레시피 입력시 필요한 내용
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeTool> recipeTools = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeLike> recipeLikes = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeBookMark> recipeFavorites = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeRating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SearchHistory> searchHistories = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeImage> recipeImages = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return Objects.equals(id, recipe.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void addRecipeIngredient(RecipeIngredient recipeIngredient) {
        recipeIngredient.setRecipe(this);
        recipeIngredients.add(recipeIngredient);
    }

    public void addRecipeTool(RecipeTool recipeTool) {
        recipeTool.setRecipe(this);
        recipeTools.add(recipeTool);
    }

    public void addRecipeStep(RecipeStep recipeStep) {
        recipeSteps.add(recipeStep);
        recipeStep.setRecipe(this);
    }

    public void addRecipeImage(RecipeImage image) {
        image.setRecipe(this);
        this.recipeImages.add(image);
    }


    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDifficulty(DifficultyType difficulty) {
        this.difficulty = difficulty;
    }

    public void setCategory(CategoryType category) {
        this.category = category;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }
}
