package org.example.recipe_match_backend.global.exception.type;

import org.example.recipe_match_backend.global.exception.BusinessException;
import org.example.recipe_match_backend.global.exception.ErrorCode;

public class TypeNotFoundException extends BusinessException {
    public TypeNotFoundException() {
        super(ErrorCode.TYPE_NOT_FOUND);
    }
}
