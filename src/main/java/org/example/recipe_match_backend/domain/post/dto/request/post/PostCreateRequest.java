package org.example.recipe_match_backend.domain.post.dto.request.post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateRequest {
    private String uid;
    private String title;
    private String content;
}
