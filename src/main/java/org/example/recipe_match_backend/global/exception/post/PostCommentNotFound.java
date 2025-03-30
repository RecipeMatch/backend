package org.example.recipe_match_backend.global.exception.post;

import org.example.recipe_match_backend.global.exception.BusinessException;
import org.example.recipe_match_backend.global.exception.ErrorCode;

public class PostCommentNotFound extends BusinessException {
    public PostCommentNotFound() {
        super(ErrorCode.POST_COMMENT_NOT_FOUND);
    }
}
