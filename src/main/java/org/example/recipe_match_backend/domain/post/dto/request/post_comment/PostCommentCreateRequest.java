package org.example.recipe_match_backend.domain.post.dto.request.post_comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCommentCreateRequest {
    private String uid;      // 댓글 작성자 식별자 (예: 이메일)
    private String content;  // 댓글 내용
}
