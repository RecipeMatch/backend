package org.example.recipe_match_backend.domain.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.recipe_match_backend.domain.ingredient.domain.Ingredient;
import org.example.recipe_match_backend.domain.recipe.domain.*;
import org.example.recipe_match_backend.domain.tool.domain.Tool;
import org.example.recipe_match_backend.domain.user.dto.request.AddInfoRequest;
import org.example.recipe_match_backend.global.entity.BaseEntity;
import org.example.recipe_match_backend.domain.post.domain.Post;
import org.example.recipe_match_backend.domain.post.domain.PostComment;
import org.example.recipe_match_backend.domain.searchhistory.domain.SearchHistory;
import org.example.recipe_match_backend.type.AllergyType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uid;

    @Column(nullable = false, unique = true)
    private String nickname;

    private String phoneNumber;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_allergies", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "allergy_type")
    @Enumerated(EnumType.STRING)
    private List<AllergyType> allergies = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Recipe> recipes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<RecipeLike> recipeLikes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeBookMark> recipeBookmarks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeComment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeRating> ratings = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SearchHistory> searchHistories = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> postComments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTool> userTools = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserIngredient> userIngredients = new ArrayList<>();

    public void updateInfo(AddInfoRequest request){
        this.nickname = request.getNickname();
        this.phoneNumber = request.getPhoneNumber();
    }
    public void addRecipe(Recipe recipe) {
        this.recipes.add(recipe);
    }

    public void addUserTool(UserTool userTool) {
        this.userTools.add(userTool);
        userTool.addUser(this);
    }

    public void addUserIngredient(UserIngredient userIngredient) {
        this.userIngredients.add(userIngredient);
        userIngredient.addUser(this);
    }

    // user에 allergy 부분 추가
    public void addAllergy(List<AllergyType> allergies) {
        this.allergies = allergies;
    }

    // 마찬가지로 Tool, Ingredient용 메서드도 가능
    public void addTool(Tool tool) {
        UserTool ut = new UserTool(this, tool);
        this.userTools.add(ut);
        tool.getUserTools().add(ut);
    }

    public void addIngredient(Ingredient ingredient) {
        UserIngredient ui = new UserIngredient(this, ingredient);
        this.userIngredients.add(ui);
        ingredient.getUserIngredients().add(ui);
    }
}