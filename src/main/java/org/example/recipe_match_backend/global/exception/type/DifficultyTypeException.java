package org.example.recipe_match_backend.global.exception.type;

import org.example.recipe_match_backend.global.exception.BusinessException;
import org.example.recipe_match_backend.global.exception.ErrorCode;

public class DifficultyTypeException extends BusinessException {
    public DifficultyTypeException() {
        super(ErrorCode.DIFFICULTY_TYPE_NOT_FOUND);
    }
}
