package org.example.recipe_match_backend.domain.post.dto.response.post;

import jakarta.persistence.Column;
import org.example.recipe_match_backend.domain.post.domain.PostComment;

import java.util.List;

public class PostResponse {

    private Long postId;
    private String userUid;
    private String title;
    private String content;
    private List<PostComment> comments;

}
