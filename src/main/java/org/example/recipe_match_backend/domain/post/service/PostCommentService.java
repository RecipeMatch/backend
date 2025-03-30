package org.example.recipe_match_backend.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.recipe_match_backend.domain.post.domain.Post;
import org.example.recipe_match_backend.domain.post.dto.request.post_comment.PostCommentCreateRequest;
import org.example.recipe_match_backend.domain.post.dto.request.post_comment.PostCommentUpdateRequest;
import org.example.recipe_match_backend.domain.post.dto.response.post_comment.PostCommentResponse;
import org.example.recipe_match_backend.domain.post.repository.PostCommentRepository;
import org.example.recipe_match_backend.domain.post.repository.PostRepository;
import org.example.recipe_match_backend.domain.user.repository.UserRepository;
import org.example.recipe_match_backend.global.exception.post.PostNotFound;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<PostCommentResponse> findAll(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);

        return post.getPostComments().stream()
                .map(PostCommentResponse::from)
                .toList();
    }

    @Transactional
    public void create(Long postId, PostCommentCreateRequest request) {

    }

    @Transactional
    public void update(Long commentId, PostCommentUpdateRequest request) {

    }

    @Transactional
    public void delete(Long commentId) {

    }
}
