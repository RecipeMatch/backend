package org.example.recipe_match_backend.domain.post.dto.response.post;

import lombok.Getter;
import org.example.recipe_match_backend.domain.post.domain.Post;
import org.example.recipe_match_backend.domain.post.domain.PostComment;
import org.example.recipe_match_backend.domain.post.dto.response.post_comment.PostCommentResponse;

import java.util.List;

@Getter
public class PostResponse {

    private Long postId;
    private String title;
    private String content;
    private List<PostCommentResponse> comments; // List<PostComment> → List<PostCommentResponse>

    public static PostResponse from(Post post) {
        PostResponse response = new PostResponse();
        response.postId = post.getId();
        response.title = post.getTitle();
        response.content = post.getContent();

        // PostComment 엔티티들을 PostCommentResponse로 변환
        response.comments = post.getPostComments().stream()
                .map(PostCommentResponse::from)
                .toList();

        return response;
    }
}
