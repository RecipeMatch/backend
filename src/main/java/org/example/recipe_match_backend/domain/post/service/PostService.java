package org.example.recipe_match_backend.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.recipe_match_backend.domain.post.domain.Post;
import org.example.recipe_match_backend.domain.post.dto.request.post.PostCreateRequest;
import org.example.recipe_match_backend.domain.post.dto.request.post.PostRequest;
import org.example.recipe_match_backend.domain.post.dto.response.post.PostResponse;
import org.example.recipe_match_backend.domain.post.repository.PostRepository;
import org.example.recipe_match_backend.domain.user.domain.User;
import org.example.recipe_match_backend.domain.user.repository.UserRepository;
import org.example.recipe_match_backend.global.exception.post.PostNotFound;
import org.example.recipe_match_backend.global.exception.user.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public PostResponse find(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);
        return PostResponse.from(post);
    }

    public List<PostResponse> findAll() {
        return postRepository.findAll().stream()
                .map(PostResponse::from)
                .toList();
    }

    @Transactional(readOnly = false)
    public void create(PostCreateRequest request) {
        User user = userRepository.findByUid(request.getUid())
                .orElseThrow(UserNotFoundException::new);

        Post post = Post.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        postRepository.save(post);
    }

    @Transactional(readOnly = false)
    public void update(PostRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(PostNotFound::new);

        post.update(request.getTitle(), request.getContent());
    }

    @Transactional(readOnly = false)
    public void delete(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);
        postRepository.delete(post);
    }
}
