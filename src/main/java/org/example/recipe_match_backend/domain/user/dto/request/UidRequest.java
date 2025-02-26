package org.example.recipe_match_backend.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UidRequest {
    private String uid;
}
