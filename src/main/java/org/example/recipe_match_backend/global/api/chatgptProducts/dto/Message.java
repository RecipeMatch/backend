package org.example.recipe_match_backend.global.api.chatgptProducts.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Message {
    private String role;
    private String content;
}
