package org.example.recipe_match_backend.global.exception.type;

import org.example.recipe_match_backend.global.exception.BusinessException;
import org.example.recipe_match_backend.global.exception.ErrorCode;

public class AllergyTypeException extends BusinessException {
    public AllergyTypeException(String allergyType) {
        super(ErrorCode.valueOf(ErrorCode.ALLERGY_TYPE_NOT_FOUND + allergyType));
    }
}
