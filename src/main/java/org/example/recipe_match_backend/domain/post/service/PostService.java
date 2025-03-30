package org.example.recipe_match_backend.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.recipe_match_backend.domain.post.domain.Post;
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
import java.util.Optional;

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
        return PostResponse.of(post);
    }

    public List<PostResponse> findAll() {
        return postRepository.findAll().stream()
                .map(PostResponse::of)
                .toList();
    }

    public void create(PostRequest request) {
        User user = userRepository.findByUid(request.getUid())
                .orElseThrow(UserNotFoundException::new);

        Post post = Post.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        postRepository.save(post);
    }

    public void update(PostRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(PostNotFound::new);

        post.update(request.getTitle(), request.getContent());
    }

    public void delete(Long postId) {

    }
}
