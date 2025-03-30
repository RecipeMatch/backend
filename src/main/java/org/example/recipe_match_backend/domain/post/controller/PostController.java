package org.example.recipe_match_backend.domain.post.controller;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.post.dto.request.post.PostRequest;
import org.example.recipe_match_backend.domain.post.dto.response.post.PostResponse;
import org.example.recipe_match_backend.domain.post.service.PostService;
import org.example.recipe_match_backend.domain.user.dto.request.AddInfoRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    @GetMapping("/post/{postId}")
    public ResponseEntity<PostResponse> find(@PathVariable Long postId){
        return ResponseEntity.ok(postService.find(postId));
    }

    @GetMapping("/post")
    public List<PostResponse> findAll(){
        return postService.findAll();
    }

    @PostMapping("/post")
    public ResponseEntity<Void> create(@RequestBody PostRequest request){
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/post")
    public ResponseEntity<Void> update(@RequestBody PostRequest request) {
        postService.update(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId){
        postService.delete(postId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
