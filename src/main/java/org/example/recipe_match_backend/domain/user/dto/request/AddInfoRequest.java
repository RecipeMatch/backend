package org.example.recipe_match_backend.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.recipe_match_backend.domain.user.domain.User;
import org.example.recipe_match_backend.type.AllergyType;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddInfoRequest {

    private String uid;
    private String nickname;
    private String phoneNumber;

    private List<AllergyType> allergyNames;
    private List<String> toolNames;
    private List<String> ingredientNames;
}
