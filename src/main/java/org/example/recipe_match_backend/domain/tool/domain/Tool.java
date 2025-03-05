package org.example.recipe_match_backend.domain.tool.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeTool;
import org.example.recipe_match_backend.domain.user.domain.UserTool;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Tool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String toolName;

    @OneToMany(mappedBy = "tool")
    private List<RecipeTool> recipeTools = new ArrayList<>();

    @OneToMany(mappedBy = "tool", cascade = CascadeType.PERSIST)
    private List<UserTool> userTools = new ArrayList<>();

    public void addRecipeTool(RecipeTool recipeTool) {
        this.recipeTools.add(recipeTool);
    }

    public void addUserTool(UserTool userTool) {
        this.userTools.add(userTool);
        userTool.addTool(this);
    }

    public Tool(String toolName) {
        this.toolName = toolName;
    }

}