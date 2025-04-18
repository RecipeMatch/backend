package org.example.recipe_match_backend.global.api.chatgptProducts.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Usage {
    private Integer prompt_tokens;
    private Integer completion_tokens;
    private Integer total_tokens;
}
