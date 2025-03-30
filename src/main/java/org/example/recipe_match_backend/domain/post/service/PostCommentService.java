package org.example.recipe_match_backend.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.recipe_match_backend.domain.post.dto.request.post_comment.PostCommentCreateRequest;
import org.example.recipe_match_backend.domain.post.dto.request.post_comment.PostCommentUpdateRequest;
import org.example.recipe_match_backend.domain.post.dto.response.post_comment.PostCommentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentService {


    public List<PostCommentResponse> findAll(Long postId) {
        return null;
    }

    public void create(Long postId, PostCommentCreateRequest request) {

    }

    public void update(Long commentId, PostCommentUpdateRequest request) {

    }

    public void delete(Long commentId) {

    }
}
