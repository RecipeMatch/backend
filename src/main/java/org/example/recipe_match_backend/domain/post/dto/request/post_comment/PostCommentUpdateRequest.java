package org.example.recipe_match_backend.domain.post.dto.request.post_comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCommentUpdateRequest {
    private String content;
}
