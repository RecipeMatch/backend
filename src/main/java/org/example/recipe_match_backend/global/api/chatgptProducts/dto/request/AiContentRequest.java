package org.example.recipe_match_backend.global.api.chatgptProducts.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class AiContentRequest {

    private String systemContent;
    private String userContent;
}
