package org.example.recipe_match_backend.global.api.chatgptProducts.dto.response;

import lombok.*;
import org.example.recipe_match_backend.global.api.chatgptProducts.dto.Choice;
import org.example.recipe_match_backend.global.api.chatgptProducts.dto.Usage;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class CommentResponse {
    private List<Choice> choices;
    private Usage usage;
}
