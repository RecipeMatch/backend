package org.example.recipe_match_backend.domain.post.dto.request.post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {

    private Long postId;
    private String uid;     // 사용자 이메일
    private String title;   // 게시판 제목
    private String content; // 게시판 내용


}
