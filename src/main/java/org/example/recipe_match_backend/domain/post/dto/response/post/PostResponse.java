package org.example.recipe_match_backend.domain.post.dto.response.post;

import lombok.Getter;
import org.example.recipe_match_backend.domain.post.domain.Post;
import org.example.recipe_match_backend.domain.post.domain.PostComment;

import java.util.List;

@Getter
public class PostResponse {

    private Long postId;
    private String title;
    private String content;
    private List<PostComment> comments;

    public static PostResponse from(Post post) {
        PostResponse response = new PostResponse();
        response.postId = post.getId();
        response.title = post.getTitle();
        response.content = post.getContent();
        response.comments = post.getPostComments();
        return response;
    }
}
