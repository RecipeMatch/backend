package org.example.recipe_match_backend.domain.post.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.recipe_match_backend.global.entity.BaseEntity;
import org.example.recipe_match_backend.domain.user.domain.User;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder(toBuilder = true)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    @Column(length = 2000)
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> postComments = new ArrayList<>();

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
