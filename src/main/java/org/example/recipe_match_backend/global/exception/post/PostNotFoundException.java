package org.example.recipe_match_backend.global.exception.post;

import org.example.recipe_match_backend.global.exception.BusinessException;
import org.example.recipe_match_backend.global.exception.ErrorCode;

public class PostNotFoundException extends BusinessException {
    public PostNotFoundException() {
        super(ErrorCode.POST_NOT_FOUND);
    }

}
