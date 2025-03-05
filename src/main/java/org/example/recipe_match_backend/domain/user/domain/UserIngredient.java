package org.example.recipe_match_backend.domain.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.recipe_match_backend.domain.ingredient.domain.Ingredient;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // N:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // N:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    public void addIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public void addUser(User user) {
        this.user = user;
    }

    public UserIngredient(User user, Ingredient ingredient) {
        this.user = user;
        this.ingredient = ingredient;
    }
}
