package org.example.recipe_match_backend.domain.post.dto.response.post_comment;

import lombok.Getter;
import org.example.recipe_match_backend.domain.post.domain.PostComment;

@Getter
public class PostCommentResponse {
    private Long commentId;
    private String uid;
    private String content;

    public static PostCommentResponse from(PostComment comment) {
        PostCommentResponse response = new PostCommentResponse();
        response.commentId = comment.getId();
        response.uid = comment.getUser().getUid(); // User 클래스에 uid 필드가 있다고 가정
        response.content = comment.getContent();
        return response;
    }
}
