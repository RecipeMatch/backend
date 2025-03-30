package org.example.recipe_match_backend.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.recipe_match_backend.domain.post.domain.Post;
import org.example.recipe_match_backend.domain.post.domain.PostComment;
import org.example.recipe_match_backend.domain.post.dto.request.post_comment.PostCommentCreateRequest;
import org.example.recipe_match_backend.domain.post.dto.request.post_comment.PostCommentUpdateRequest;
import org.example.recipe_match_backend.domain.post.dto.response.post_comment.PostCommentResponse;
import org.example.recipe_match_backend.domain.post.repository.PostCommentRepository;
import org.example.recipe_match_backend.domain.post.repository.PostRepository;
import org.example.recipe_match_backend.domain.user.domain.User;
import org.example.recipe_match_backend.domain.user.repository.UserRepository;
import org.example.recipe_match_backend.global.exception.post.PostCommentNotFoundException;
import org.example.recipe_match_backend.global.exception.post.PostNotFoundException;
import org.example.recipe_match_backend.global.exception.user.UserNotFoundException;
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
                .orElseThrow(PostNotFoundException::new);

        return post.getPostComments().stream()
                .map(PostCommentResponse::from)
                .toList();
    }

    @Transactional
    public void create(Long postId, PostCommentCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        User user = userRepository.findByUid(request.getUid())
                .orElseThrow(UserNotFoundException::new);

        PostComment comment = PostComment.builder()
                .post(post)
                .user(user)
                .content(request.getContent())
                .build();

        postCommentRepository.save(comment);
    }

    @Transactional
    public void update(Long commentId, PostCommentUpdateRequest request) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(PostCommentNotFoundException::new);

        comment.updateContent(request.getContent());
    }

    @Transactional
    public void delete(Long commentId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(PostCommentNotFoundException::new);
        postCommentRepository.delete(comment);
    }
}
