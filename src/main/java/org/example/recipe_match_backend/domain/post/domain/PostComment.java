package org.example.recipe_match_backend.domain.post.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.recipe_match_backend.global.entity.BaseEntity;
import org.example.recipe_match_backend.domain.user.domain.User;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder(toBuilder = true)
public class PostComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(length = 1000)
    private String content;

    public void updateContent(String content) {
        this.content = content;
    }
}
