package org.example.recipe_match_backend.domain.recipe.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.recipe_match_backend.global.entity.BaseEntity;
import org.example.recipe_match_backend.domain.user.domain.User;

import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecipeComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @Column(length = 1000)
    private String content;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RecipeComment that = (RecipeComment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // 댓글 내용 수정 메서드
    public void updateContent(String content) {
        this.content = content;
    }

}
