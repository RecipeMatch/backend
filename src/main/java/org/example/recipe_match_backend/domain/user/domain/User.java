package org.example.recipe_match_backend.domain.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.recipe_match_backend.domain.recipe.domain.*;
import org.example.recipe_match_backend.domain.user.dto.request.AddInfoRequest;
import org.example.recipe_match_backend.global.entity.BaseEntity;
import org.example.recipe_match_backend.domain.post.domain.Post;
import org.example.recipe_match_backend.domain.post.domain.PostComment;
import org.example.recipe_match_backend.domain.searchhistory.domain.SearchHistory;

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

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Recipe> recipes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<RecipeLike> recipeLikes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<RecipeBookMark> recipeBookmarks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<RecipeComment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<RecipeRating> ratings = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<SearchHistory> searchHistories = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<PostComment> postComments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<UserAllergy> userAllergies = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<UserTool> userTools = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<UserIngredient> userIngredients = new ArrayList<>();

    public void updateInfo(AddInfoRequest request){
        this.nickname = request.getNickname();
        this.phoneNumber = request.getPhoneNumber();
    }
    public void addRecipe(Recipe recipe) {
        this.recipes.add(recipe);
    }
}
