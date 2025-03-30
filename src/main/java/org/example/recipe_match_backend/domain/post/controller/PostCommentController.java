package org.example.recipe_match_backend.domain.post.controller;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.post.dto.request.post_comment.PostCommentCreateRequest;
import org.example.recipe_match_backend.domain.post.dto.request.post_comment.PostCommentUpdateRequest;
import org.example.recipe_match_backend.domain.post.dto.response.post_comment.PostCommentResponse;
import org.example.recipe_match_backend.domain.post.service.PostCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostCommentController {

    private final PostCommentService postCommentService;

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<PostCommentResponse>> findAll(@PathVariable Long postId) {
        List<PostCommentResponse> responses = postCommentService.findAll(postId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<Void> create(@PathVariable Long postId,
                                              @RequestBody PostCommentCreateRequest request) {
        postCommentService.create(postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/comment/{commentId}")
    public ResponseEntity<Void> update(@PathVariable Long commentId,
                                              @RequestBody PostCommentUpdateRequest request) {
        postCommentService.update(commentId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        postCommentService.delete(commentId);
        return ResponseEntity.ok().build();
    }

}
