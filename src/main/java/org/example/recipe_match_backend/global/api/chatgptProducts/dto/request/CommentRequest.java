package org.example.recipe_match_backend.global.api.chatgptProducts.dto.request;

import lombok.*;
import org.example.recipe_match_backend.global.api.chatgptProducts.dto.Message;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class CommentRequest {
    private String model;
    private List<Message> message;
}
