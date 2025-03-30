package org.example.recipe_match_backend.domain.post.dto.request.post_comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCommentCreateRequest {
    private String uid;
    private String content;
}
