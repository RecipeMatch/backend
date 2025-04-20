package org.example.recipe_match_backend.global.config;

import org.example.recipe_match_backend.global.api.chatgptProducts.dto.request.CommentRequest;
import org.example.recipe_match_backend.global.api.chatgptProducts.dto.response.CommentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(name = "ChatgptClient", url = "https://api.openai.com/v1/chat/completions")
public interface ChatgptClient {
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    CommentResponse getGptComment(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CommentRequest commentRequest
    );
}
